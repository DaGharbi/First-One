import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CdeVersDeltaRequest {
  codeAgence: string;
  codeDevise: string;
  datePass: string;
  naturePass: string;
  descriptionClient?: string;
  isBillets?: boolean;
  montant: number;
  cdeVers: 0 | 1 | 2;
}

export interface CdeVersDelta {
  codeAgence: string;
  libAgence: string | null;
  codeDevise: string;
  libDevise: string | null;
  datePass: string;
  naturePass: string;
  descriptionClient?: string;
  isBillets?: boolean;
  montant: number;
  cdeVers: 0 | 1 | 2;
}

@Injectable({
  providedIn: 'root'
})
export class CdeVersDeltaService {
  private readonly API_URL = '/api/cde-vers-delta';

  constructor(private http: HttpClient) { }

  create(payload: CdeVersDeltaRequest): Observable<CdeVersDelta> {
    return this.http.post<CdeVersDelta>(this.API_URL, payload);
  }

  listForSameCaisseCentrale(codeAgence: string): Observable<CdeVersDelta[]> {
    return this.http.get<CdeVersDelta[]>(this.API_URL, {
      params: { codeAgence }
    });
  }

  listForAgency(codeAgence: string): Observable<CdeVersDelta[]> {
    return this.http.get<CdeVersDelta[]>(`${this.API_URL}/agency`, {
      params: { codeAgence }
    });
  }
}
