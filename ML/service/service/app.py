from __future__ import annotations

import threading
from datetime import date

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

from .predictor import PredictionError, get_predictor


class PredictionRequest(BaseModel):
    codeAgence: str = Field(min_length=1, description="Code agence")
    startDate: date = Field(description="Date de debut")
    endDate: date = Field(description="Date de fin")


class PredictionResponse(BaseModel):
    codeAgence: str
    targetDate: str
    predictedSens: str
    probabilityC: float
    confidence: float
    estimatedAmount: float
    modelVersion: str
    featureStrategy: str


class PredictionDayResponse(BaseModel):
    targetDate: str
    predictedSens: str
    probabilityC: float
    confidence: float
    estimatedAmount: float


class PredictionWindowResponse(BaseModel):
    codeAgence: str
    startDate: str
    endDate: str
    predictions: list[PredictionDayResponse]
    modelVersion: str
    featureStrategy: str


app = FastAPI(title="encaisse-ml-service", version="1.0.0")
_warmup_error: str | None = None


def _warmup_predictor() -> None:
    global _warmup_error
    try:
        get_predictor()
        _warmup_error = None
    except Exception as exc:  # pragma: no cover - operational warmup path
        _warmup_error = str(exc)


@app.on_event("startup")
def startup_warmup() -> None:
    threading.Thread(target=_warmup_predictor, daemon=True).start()


@app.get("/health")
def health() -> dict[str, str]:
    warmed = get_predictor.cache_info().currsize > 0
    status = "UP" if warmed and _warmup_error is None else "WARMING_UP"

    payload = {
        "status": status,
        "service": "encaisse-ml-service",
    }

    if _warmup_error:
        payload["lastError"] = _warmup_error

    return payload


@app.post("/ml/encaisse-estimation", response_model=PredictionWindowResponse)
def estimate_encaisse(payload: PredictionRequest) -> PredictionWindowResponse:
    if get_predictor.cache_info().currsize == 0:
        raise HTTPException(
            status_code=503,
            detail="Le modele ML est en cours d'initialisation. Reessayez dans quelques instants.",
        )

    try:
        result = get_predictor().predict_window(payload.codeAgence, payload.startDate, payload.endDate)
    except PredictionError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    except Exception as exc:  # pragma: no cover - operational endpoint
        raise HTTPException(status_code=500, detail=f"Erreur ML: {exc}") from exc

    return PredictionWindowResponse(
        codeAgence=result.code_agence,
        startDate=result.start_date,
        endDate=result.end_date,
        predictions=[
            PredictionDayResponse(
                targetDate=prediction.requested_date,
                predictedSens=prediction.predicted_sens,
                probabilityC=prediction.probability_c,
                confidence=prediction.confidence,
                estimatedAmount=prediction.estimated_amount,
            )
            for prediction in result.predictions
        ],
        modelVersion=result.model_version,
        featureStrategy=result.feature_strategy,
    )
