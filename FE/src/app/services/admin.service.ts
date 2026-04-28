import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AdminUser {
  usrMat: string;
  codeAgence: string;
  codeProfil: string;
  usrName: string;
  suspendu: string;
}

export interface Agence {
  codeAgence: string;
  codeCent: string;
  codeGroupe: string;
  libAgence: string;
  codeIbs: string | null;
  codeCaissCent: string;
  nbreGab: number | null;
  matChefAgence: string | null;
}

export interface Groupe {
  codeGroupe: string;
  libGroupe: string;
  matChefGroupe: string | null;
  nameChefGroupe: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly apiUrl = '/api';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${this.apiUrl}/users`);
  }

  createUser(payload: AdminUser): Observable<AdminUser> {
    return this.http.post<AdminUser>(`${this.apiUrl}/users`, payload);
  }

  updateUser(usrMat: string, payload: AdminUser): Observable<AdminUser> {
    return this.http.put<AdminUser>(`${this.apiUrl}/users/${usrMat}`, payload);
  }

  deleteUser(usrMat: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${usrMat}`);
  }

  getAgences(): Observable<Agence[]> {
    return this.http.get<Agence[]>(`${this.apiUrl}/agences`);
  }

  createAgence(payload: Agence): Observable<Agence> {
    return this.http.post<Agence>(`${this.apiUrl}/agences`, payload);
  }

  updateAgence(codeAgence: string, payload: Agence): Observable<Agence> {
    return this.http.put<Agence>(`${this.apiUrl}/agences/${codeAgence}`, payload);
  }

  deleteAgence(codeAgence: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/agences/${codeAgence}`);
  }

  getGroupes(): Observable<Groupe[]> {
    return this.http.get<Groupe[]>(`${this.apiUrl}/groupes`);
  }

  createGroupe(payload: Groupe): Observable<Groupe> {
    return this.http.post<Groupe>(`${this.apiUrl}/groupes`, payload);
  }

  updateGroupe(codeGroupe: string, payload: Groupe): Observable<Groupe> {
    return this.http.put<Groupe>(`${this.apiUrl}/groupes/${codeGroupe}`, payload);
  }

  deleteGroupe(codeGroupe: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/groupes/${codeGroupe}`);
  }
}
