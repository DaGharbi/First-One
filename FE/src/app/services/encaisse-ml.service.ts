import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EncaissePredictionPayload {
  codeAgence: string;
  startDate: string;
  endDate: string;
}

export interface EncaissePredictionDay {
  targetDate: string;
  predictedSens: string;
  probabilityC: number;
  confidence: number;
  estimatedAmount: number;
}

export interface EncaissePredictionResult {
  codeAgence: string;
  startDate: string;
  endDate: string;
  predictions: EncaissePredictionDay[];
  modelVersion: string;
  featureStrategy: string;
}

@Injectable({
  providedIn: 'root'
})
export class EncaisseMlService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/ml';

  estimate(payload: EncaissePredictionPayload): Observable<EncaissePredictionResult> {
    return this.http.post<EncaissePredictionResult>(`${this.apiUrl}/encaisse-estimation`, payload);
  }
}
