import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CdeMonnaiesRequest {
  codeAgence: string;
  dateCdeMonnaies: string;
  p100M: number;
  p10M: number;
  p1Dn: number;
  p20M: number;
  p500M: number;
  p50M: number;
  p5M: number;
}

export interface CdeMonnaies {
  codeAgence: string;
  libAgence: string | null;
  codeDevise: string;
  libDevise: string | null;
  dateCdeMonnaies: string;
  p100M: number;
  p10M: number;
  p1Dn: number;
  p20M: number;
  p500M: number;
  p50M: number;
  p5M: number;
}

@Injectable({
  providedIn: 'root'
})
export class CdeMonnaiesService {
  private readonly API_URL = '/api/cde-monnaies';

  constructor(private http: HttpClient) {}

  create(payload: CdeMonnaiesRequest): Observable<CdeMonnaies> {
    return this.http.post<CdeMonnaies>(this.API_URL, payload);
  }

  listForSameCaisseCentrale(codeAgence: string): Observable<CdeMonnaies[]> {
    return this.http.get<CdeMonnaies[]>(this.API_URL, {
      params: { codeAgence }
    });
  }

  listForAgency(codeAgence: string): Observable<CdeMonnaies[]> {
    return this.http.get<CdeMonnaies[]>(`${this.API_URL}/agency`, {
      params: { codeAgence }
    });
  }
}
