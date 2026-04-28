import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, timeout } from 'rxjs';

export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthUser {
  usrMat: string;
  codeAgence: string;
  codeProfil: string;
  usrName: string;
  suspendu: string | null;
  libAgence: string | null;
  libCaisseCent: string | null;
  libGroupe: string | null;
}

export interface AuthResponse {
  token: string;
  user: AuthUser | null;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = '/api';
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {}

  register(credentials: AuthRequest): Observable<any> {
    return this.http.post(`${this.API_URL}/auth/register`, credentials);
  }

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/auth/login`, credentials)
      .pipe(
        timeout(10000),
        tap(response => {
          if (response.token) {
            this.setToken(response.token);
            this.setUser(response.user);
            this.isAuthenticatedSubject.next(true);
          }
        })
      );
  }

  logout(): void {
    this.removeToken();
    this.removeUser();
    this.isAuthenticatedSubject.next(false);
  }

  private setToken(token: string): void {
    const storage = this.getStorage();

    if (storage) {
      storage.setItem(this.TOKEN_KEY, token);
    }
  }

  private getToken(): string | null {
    return this.getStorage()?.getItem(this.TOKEN_KEY) ?? null;
  }

  private setUser(user: AuthUser | null): void {
    const storage = this.getStorage();

    if (!storage) {
      return;
    }

    if (user) {
      storage.setItem(this.USER_KEY, JSON.stringify(user));
      return;
    }

    storage.removeItem(this.USER_KEY);
  }

  getCurrentUser(): AuthUser | null {
    const rawUser = this.getStorage()?.getItem(this.USER_KEY);

    if (!rawUser) {
      return null;
    }

    try {
      return JSON.parse(rawUser) as AuthUser;
    } catch {
      this.removeUser();
      return null;
    }
  }

  private removeToken(): void {
    this.getStorage()?.removeItem(this.TOKEN_KEY);
  }

  private removeUser(): void {
    this.getStorage()?.removeItem(this.USER_KEY);
  }

  private hasToken(): boolean {
    return !!this.getToken();
  }

  private getStorage(): Storage | null {
    return typeof localStorage === 'undefined' ? null : localStorage;
  }

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    });
  }

  isAuthenticated(): boolean {
    return this.hasToken();
  }

  getCurrentAuthRole(): string | null {
    return this.resolveRoleFromCodeProfil(this.getCurrentUser()?.codeProfil ?? null);
  }

  isAdmin(): boolean {
    return this.getCurrentAuthRole() === 'ADMIN';
  }

  isSecurity(): boolean {
    return this.getCurrentAuthRole() === 'SECURITY';
  }

  isCc(): boolean {
    return this.getCurrentAuthRole() === 'CC';
  }

  isAgent(): boolean {
    return this.getCurrentAuthRole() === 'AGENT';
  }

  canAccessBusinessSections(): boolean {
    const role = this.getCurrentAuthRole();
    return role === 'ADMIN' || role === 'CC';
  }

  canAccessAdministration(): boolean {
    const role = this.getCurrentAuthRole();
    return role === 'ADMIN' || role === 'SECURITY';
  }

  canAccessStatistics(): boolean {
    const role = this.getCurrentAuthRole();
    return role === 'ADMIN' || role === 'CC' || role === 'SECURITY';
  }

  resolveRoleFromCodeProfil(codeProfil: string | null | undefined): string | null {
    const normalizedCodeProfil = codeProfil?.trim().toUpperCase();

    switch (normalizedCodeProfil) {
      case 'ADM':
        return 'ADMIN';
      case 'SEC':
        return 'SECURITY';
      case 'RAG':
        return 'AGENT';
      case 'RCC':
        return 'CC';
      default:
        return null;
    }
  }
}
