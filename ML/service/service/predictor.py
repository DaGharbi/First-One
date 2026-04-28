from __future__ import annotations

from dataclasses import dataclass
from datetime import date
from functools import lru_cache
import re
import os
from pathlib import Path
from typing import Any

import numpy as np
import pandas as pd
import xgboost as xgb


REQUIRED_ACCOUNT_LABELS = {
    "CAISSE DINARS",
    "CAISSES EN DINARS",
}

FEATURE_COLS = [
    "DAY_OF_WEEK",
    "DAY_OF_MONTH",
    "MONTH",
    "QUARTER",
    "IS_MONTH_END",
    "IS_MONTH_START",
    "IS_WEEKEND",
    "MONTH_SIN",
    "MONTH_COS",
    "DOW_SIN",
    "DOW_COS",
    "MONTANT_LAG_1",
    "MONTANT_LAG_3",
    "MONTANT_LAG_7",
    "MONTANT_LAG_14",
    "MONTANT_LAG_30",
    "SENS_LAG_1",
    "SENS_LAG_3",
    "SENS_LAG_7",
    "SENS_LAG_14",
    "SENS_LAG_30",
    "MONTANT_ROLL_MEAN_7",
    "MONTANT_ROLL_STD_7",
    "MONTANT_ROLL_MAX_7",
    "MONTANT_ROLL_MIN_7",
    "MONTANT_ROLL_MEAN_14",
    "MONTANT_ROLL_STD_14",
    "MONTANT_ROLL_MEAN_30",
    "MONTANT_ROLL_STD_30",
    "SOLDE_COURANT",
    "SOLDE_INITIAL",
    "CODE_AGENCE",
    "DEV",
    "CODE_OPERATION",
    "NB_TOTAL_OPS",
    "MONTANT_MOYEN_AGE",
    "PROP_C_AGE",
    "LOG_NB_OPERATION",
]


class PredictionError(RuntimeError):
    pass


@dataclass(frozen=True)
class PredictionResult:
    code_agence: str
    requested_date: str
    predicted_sens: str
    probability_c: float
    confidence: float
    estimated_amount: float
    model_version: str
    feature_strategy: str


@dataclass(frozen=True)
class ForecastWindowResult:
    code_agence: str
    start_date: str
    end_date: str
    predictions: list[PredictionResult]
    model_version: str
    feature_strategy: str


class EncaissePredictor:
    def __init__(self, ml_root: Path) -> None:
        self.ml_root = ml_root
        self.model_dir = ml_root / "models"
        self.transactions = self._load_transactions()
        self.feature_history = self._prepare_feature_history(self.transactions)
        self.xgb_classifier = self._load_classifier()
        self.xgb_regressor = self._load_regressor()

    def predict(self, code_agence: str, target_date: date) -> PredictionResult:
        agency_code = int(pd.to_numeric(code_agence, errors="coerce"))
        prediction_date = pd.Timestamp(target_date)

        agency_history = self.feature_history[
            (self.feature_history["CODE_AGENCE"] == agency_code)
            & (self.feature_history["PERIODE"] < prediction_date)
        ].copy()

        if agency_history.empty:
            raise PredictionError(
                f"Aucun historique exploitable trouve pour l'agence {agency_code} avant le {prediction_date.date().isoformat()}."
            )

        agency_history = agency_history.sort_values("PERIODE")
        inference_row = self._build_inference_row(agency_history, prediction_date)
        encoded = self._prepare_model_input(inference_row)

        proba_c = float(self.xgb_classifier.predict_proba(encoded)[0, 1])
        predicted_log_amount = float(self.xgb_regressor.predict(encoded)[0])
        estimated_amount = float(np.expm1(predicted_log_amount))

        predicted_sens = "C" if proba_c >= 0.5 else "D"
        confidence = max(proba_c, 1.0 - proba_c)

        return PredictionResult(
            code_agence=str(agency_code),
            requested_date=prediction_date.date().isoformat(),
            predicted_sens=predicted_sens,
            probability_c=round(proba_c, 4),
            confidence=round(confidence, 4),
            estimated_amount=round(estimated_amount, 2),
            model_version="xgboost-json-v1",
            feature_strategy=(
                "Historique agence strictement anterieur a la date envoyee; "
                "features reconstruites pour cette date cible a partir de l'historique disponible."
            ),
        )

    def predict_window(self, code_agence: str, start_date: date, end_date: date) -> ForecastWindowResult:
        agency_code = int(pd.to_numeric(code_agence, errors="coerce"))
        first_prediction_date = pd.Timestamp(start_date)
        last_prediction_date = pd.Timestamp(end_date)

        if last_prediction_date < first_prediction_date:
            raise PredictionError("La date de fin doit etre superieure ou egale a la date de debut.")

        rolling_transactions = self.transactions[
            (self.transactions["CODE_AGENCE"] == agency_code)
            & (self.transactions["PERIODE"] < first_prediction_date)
        ][self.transactions.columns].copy()

        if rolling_transactions.empty:
            raise PredictionError(
                f"Aucun historique exploitable trouve pour l'agence {agency_code} avant le {first_prediction_date.date().isoformat()}."
            )

        predictions: list[PredictionResult] = []
        for prediction_date in pd.date_range(first_prediction_date, last_prediction_date, freq="D"):
            rolling_features = self._prepare_feature_history(rolling_transactions)
            agency_history = rolling_features[
                (rolling_features["CODE_AGENCE"] == agency_code)
                & (rolling_features["PERIODE"] < prediction_date)
            ].sort_values("PERIODE")

            if agency_history.empty:
                raise PredictionError(
                    f"Historique insuffisant pour construire la prediction du {prediction_date.date().isoformat()}."
                )

            inference_row = self._build_inference_row(agency_history, prediction_date)
            encoded = self._prepare_model_input(inference_row)

            proba_c = float(self.xgb_classifier.predict_proba(encoded)[0, 1])
            predicted_log_amount = float(self.xgb_regressor.predict(encoded)[0])
            estimated_amount = float(np.expm1(predicted_log_amount))
            predicted_sens = "C" if proba_c >= 0.5 else "D"
            confidence = max(proba_c, 1.0 - proba_c)

            predictions.append(
                PredictionResult(
                    code_agence=str(agency_code),
                    requested_date=prediction_date.date().isoformat(),
                    predicted_sens=predicted_sens,
                    probability_c=round(proba_c, 4),
                    confidence=round(confidence, 4),
                    estimated_amount=round(estimated_amount, 2),
                    model_version="xgboost-json-v1",
                    feature_strategy=(
                        "Plage de dates calculee de facon sequentielle; "
                        "chaque jour reconstruit ses features a partir de l'historique reel puis predit."
                    ),
                )
            )

            synthetic_transaction = self._build_synthetic_transaction(
                agency_history=agency_history,
                prediction_date=prediction_date,
                predicted_sens=predicted_sens,
                predicted_amount=estimated_amount,
            )
            rolling_transactions = pd.concat(
                [rolling_transactions, synthetic_transaction.reindex(columns=self.transactions.columns)],
                ignore_index=True,
            ).sort_values(["CODE_AGENCE", "PERIODE"]).reset_index(drop=True)

        return ForecastWindowResult(
            code_agence=str(agency_code),
            start_date=predictions[0].requested_date,
            end_date=predictions[-1].requested_date,
            predictions=predictions,
            model_version="xgboost-json-v1",
            feature_strategy=(
                "Plage de dates calculee de facon sequentielle; "
                "chaque jour utilise l'historique disponible et les predictions precedentes de la plage."
            ),
        )

    def available_agencies(self) -> list[str]:
        agencies = self.feature_history["CODE_AGENCE"].astype(str).dropna().unique().tolist()
        return sorted(agencies)

    def _load_transactions(self) -> pd.DataFrame:
        data_frames = [
            self._read_excel(self.ml_root / "2023.xlsx"),
            self._read_excel(
                self.ml_root / "2024.xlsx",
                sheet_name="Exporter la feuille de calcul",
            ),
        ]

        transactions = pd.concat(data_frames, ignore_index=True, sort=False)
        transactions.columns = [str(col).strip().upper() for col in transactions.columns]
        transactions = self._drop_duplicate_columns(transactions)

        if "INTITULE_COMPTE" in transactions.columns:
            normalized_label = transactions["INTITULE_COMPTE"].astype(str).str.strip().str.upper()
            transactions = transactions[normalized_label.isin(REQUIRED_ACCOUNT_LABELS)].copy()

        transactions["PERIODE"] = pd.to_datetime(transactions["PERIODE"], errors="coerce")
        transactions = transactions.dropna(subset=["PERIODE", "CODE_AGENCE", "DEV", "CODE_OPERATION", "SENS", "MONTANT"])

        transactions["CODE_AGENCE"] = pd.to_numeric(transactions["CODE_AGENCE"], errors="coerce")
        transactions["DEV"] = pd.to_numeric(transactions["DEV"], errors="coerce")
        transactions["CODE_OPERATION"] = pd.to_numeric(transactions["CODE_OPERATION"], errors="coerce")
        transactions["SENS"] = transactions["SENS"].astype(str).str.strip().str.upper()
        transactions["NB_OPERATION"] = pd.to_numeric(transactions["NB_OPERATION"], errors="coerce").fillna(0)
        transactions["MONTANT"] = pd.to_numeric(transactions["MONTANT"], errors="coerce").fillna(0)
        transactions = transactions.dropna(subset=["CODE_AGENCE", "DEV", "CODE_OPERATION"])
        transactions["CODE_AGENCE"] = transactions["CODE_AGENCE"].astype(int)
        transactions["DEV"] = transactions["DEV"].astype(int)
        transactions["CODE_OPERATION"] = transactions["CODE_OPERATION"].astype(int)
        transactions["MONTANT_ABS"] = transactions["MONTANT"].abs()
        transactions["SENS_BIN"] = (transactions["SENS"] == "C").astype(int)

        initial_balance = self._read_excel(self.ml_root / "Etat solde caisse agence 31 12 2022.xls")
        initial_balance.columns = [str(col).strip().upper() for col in initial_balance.columns]
        initial_balance["DCO"] = pd.to_datetime(initial_balance["DCO"], dayfirst=True, errors="coerce")
        initial_balance["SDE"] = -pd.to_numeric(initial_balance["SDE"], errors="coerce").fillna(0)

        balance_map = (
            initial_balance.rename(columns={"AGE": "CODE_AGENCE", "SDE": "SOLDE_INITIAL"})
            .assign(CODE_AGENCE=lambda frame: pd.to_numeric(frame["CODE_AGENCE"], errors="coerce"))
            .dropna(subset=["CODE_AGENCE"])
            .assign(CODE_AGENCE=lambda frame: frame["CODE_AGENCE"].astype(int))
            .sort_values("DCO")
            .groupby("CODE_AGENCE", as_index=False)["SOLDE_INITIAL"]
            .last()
        )

        transactions = transactions.merge(balance_map, on="CODE_AGENCE", how="left")
        transactions["SOLDE_INITIAL"] = transactions["SOLDE_INITIAL"].fillna(0)
        return transactions.sort_values(["CODE_AGENCE", "PERIODE"]).reset_index(drop=True)

    def _prepare_feature_history(self, transactions: pd.DataFrame) -> pd.DataFrame:
        df = transactions.sort_values(["CODE_AGENCE", "PERIODE"]).copy()

        df["DAY_OF_WEEK"] = df["PERIODE"].dt.dayofweek
        df["DAY_OF_MONTH"] = df["PERIODE"].dt.day
        df["MONTH"] = df["PERIODE"].dt.month
        df["QUARTER"] = df["PERIODE"].dt.quarter
        df["IS_MONTH_END"] = df["PERIODE"].dt.is_month_end.astype(int)
        df["IS_MONTH_START"] = df["PERIODE"].dt.is_month_start.astype(int)
        df["IS_WEEKEND"] = (df["DAY_OF_WEEK"] >= 5).astype(int)
        df["MONTH_SIN"] = np.sin(2 * np.pi * df["MONTH"] / 12)
        df["MONTH_COS"] = np.cos(2 * np.pi * df["MONTH"] / 12)
        df["DOW_SIN"] = np.sin(2 * np.pi * df["DAY_OF_WEEK"] / 7)
        df["DOW_COS"] = np.cos(2 * np.pi * df["DAY_OF_WEEK"] / 7)

        grouped = df.groupby("CODE_AGENCE")
        for lag in [1, 3, 7, 14, 30]:
            df[f"MONTANT_LAG_{lag}"] = grouped["MONTANT_ABS"].shift(lag)
            df[f"SENS_LAG_{lag}"] = grouped["SENS_BIN"].shift(lag)

        for window in [7, 14, 30]:
            rolling = grouped["MONTANT_ABS"].shift(1).rolling(window)
            df[f"MONTANT_ROLL_MEAN_{window}"] = rolling.mean().reset_index(level=0, drop=True)
            df[f"MONTANT_ROLL_STD_{window}"] = rolling.std().reset_index(level=0, drop=True)
            df[f"MONTANT_ROLL_MAX_{window}"] = rolling.max().reset_index(level=0, drop=True)
            df[f"MONTANT_ROLL_MIN_{window}"] = rolling.min().reset_index(level=0, drop=True)

        df["MONTANT_SIGNE"] = np.where(df["SENS_BIN"] == 1, df["MONTANT_ABS"], -df["MONTANT_ABS"])
        df["SOLDE_CUMUL"] = grouped["MONTANT_SIGNE"].cumsum().shift(1)
        df["SOLDE_COURANT"] = df["SOLDE_INITIAL"] + df["SOLDE_CUMUL"].fillna(0)

        agency_stats = (
            df.groupby("CODE_AGENCE")
            .agg(
                NB_TOTAL_OPS=("MONTANT_ABS", "count"),
                MONTANT_MOYEN_AGE=("MONTANT_ABS", "mean"),
                PROP_C_AGE=("SENS_BIN", "mean"),
            )
            .reset_index()
        )
        df = df.merge(agency_stats, on="CODE_AGENCE", how="left")
        df["LOG_NB_OPERATION"] = np.log1p(df["NB_OPERATION"].fillna(0))
        df["LOG_MONTANT"] = np.log1p(df["MONTANT_ABS"])
        return df

    def _build_inference_row(self, agency_history: pd.DataFrame, prediction_date: pd.Timestamp) -> pd.DataFrame:
        same_weekday = agency_history[agency_history["PERIODE"].dt.dayofweek == prediction_date.dayofweek]
        nb_operation_baseline = same_weekday["NB_OPERATION"].median() if not same_weekday.empty else agency_history["NB_OPERATION"].median()
        nb_operation_baseline = float(0 if pd.isna(nb_operation_baseline) else nb_operation_baseline)
        synthetic = self._build_synthetic_transaction(
            agency_history=agency_history,
            prediction_date=prediction_date,
            predicted_sens="C" if agency_history["SENS_BIN"].mean() >= 0.5 else "D",
            predicted_amount=0.0,
        )
        synthetic = synthetic.reindex(columns=self.transactions.columns, fill_value=np.nan)

        combined = pd.concat(
            [agency_history[self.transactions.columns], synthetic[self.transactions.columns]],
            ignore_index=True,
        )
        features = self._prepare_feature_history(combined)
        row = features.iloc[[-1]].copy()

        row["NB_TOTAL_OPS"] = float(len(agency_history))
        row["MONTANT_MOYEN_AGE"] = float(agency_history["MONTANT_ABS"].mean())
        row["PROP_C_AGE"] = float(agency_history["SENS_BIN"].mean())
        row["LOG_NB_OPERATION"] = np.log1p(nb_operation_baseline)
        return row

    def _build_synthetic_transaction(
        self,
        agency_history: pd.DataFrame,
        prediction_date: pd.Timestamp,
        predicted_sens: str,
        predicted_amount: float,
    ) -> pd.DataFrame:
        latest = agency_history.iloc[-1]
        dominant_dev = agency_history["DEV"].mode().iloc[0]
        dominant_operation = agency_history["CODE_OPERATION"].mode().iloc[0]
        same_weekday = agency_history[agency_history["PERIODE"].dt.dayofweek == prediction_date.dayofweek]
        nb_operation_baseline = same_weekday["NB_OPERATION"].median() if not same_weekday.empty else agency_history["NB_OPERATION"].median()
        nb_operation_baseline = float(0 if pd.isna(nb_operation_baseline) else nb_operation_baseline)
        sens = str(predicted_sens).upper()
        montant = float(max(predicted_amount, 0.0))

        synthetic = pd.DataFrame(
            [
                {
                    "CODE_AGENCE": int(latest["CODE_AGENCE"]),
                    "DEV": int(dominant_dev),
                    "CODE_OPERATION": int(dominant_operation),
                    "PERIODE": prediction_date,
                    "SENS": sens,
                    "NB_OPERATION": nb_operation_baseline,
                    "MONTANT": montant,
                    "MONTANT_ABS": montant,
                    "SENS_BIN": 1 if sens == "C" else 0,
                    "SOLDE_INITIAL": latest["SOLDE_INITIAL"],
                }
            ]
        )
        return synthetic

    def _prepare_model_input(self, row: pd.DataFrame) -> pd.DataFrame:
        model_input = row.reindex(columns=FEATURE_COLS).copy()
        for column in FEATURE_COLS:
            model_input[column] = pd.to_numeric(model_input[column], errors="coerce")
        return model_input.fillna(0)

    def _load_classifier(self) -> xgb.XGBClassifier:
        model_path = self.model_dir / "xgb_classification.json"
        model = xgb.XGBClassifier()
        model.load_model(model_path)
        return model

    def _load_regressor(self) -> xgb.XGBRegressor:
        model_path = self.model_dir / "xgb_regression.json"
        model = xgb.XGBRegressor()
        model.load_model(model_path)
        return model

    @staticmethod
    def _drop_duplicate_columns(frame: pd.DataFrame) -> pd.DataFrame:
        if frame.columns.is_unique:
            return frame
        return frame.loc[:, ~frame.columns.duplicated()].copy()

    @staticmethod
    def _normalize_agency_code(value: Any) -> str:
        text = str(value).strip()
        match = re.match(r"^(\d+)", text)
        if match:
            normalized = match.group(1).lstrip("0")
            return normalized or "0"
        return text

    @staticmethod
    def _read_excel(path: Path, **kwargs: Any) -> pd.DataFrame:
        if not path.exists():
            raise PredictionError(f"Fichier requis introuvable: {path}")
        return pd.read_excel(path, **kwargs)


@lru_cache(maxsize=1)
def get_predictor() -> EncaissePredictor:
    current_file = Path(__file__).resolve()
    candidates = []

    configured_root = os.getenv("ML_ROOT")
    if configured_root:
        candidates.append(Path(configured_root))

    parents = list(current_file.parents)
    if len(parents) > 2:
        candidates.append(parents[2])
        candidates.append(parents[2] / "ML")
    if len(parents) > 3:
        candidates.append(parents[3] / "ML")

    for candidate in candidates:
        if (candidate / "models" / "xgb_regression.json").exists():
            return EncaissePredictor(candidate)

    raise PredictionError("Impossible de localiser le repertoire ML contenant les donnees et les modeles.")
