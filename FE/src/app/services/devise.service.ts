import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Devise {
  codeDevise: string;
  libDevise: string | null;
}

type RawDevise = Partial<Devise> & {
  CODE_DEVISE?: string | null;
  LIB_DEVISE?: string | null;
};

@Injectable({
  providedIn: 'root'
})
export class DeviseService {
  private readonly API_URL = '/api/devises';

  constructor(private http: HttpClient) {}

  listDevises(): Observable<Devise[]> {
    return this.http.get<RawDevise[]>(this.API_URL).pipe(
      map((devises) =>
        devises
          .map((devise) => ({
            codeDevise: (devise.codeDevise ?? devise.CODE_DEVISE ?? '').trim().toUpperCase(),
            libDevise: devise.libDevise ?? devise.LIB_DEVISE ?? null,
          }))
          .filter((devise) => !!devise.codeDevise)
      )
    );
  }
}
