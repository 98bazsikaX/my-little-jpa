import { Injectable, signal, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

/** Shape returned by {@code POST /api/auth/login}. */
export interface LoginResponse {
  success: boolean;
  message: string;
  token?: string;
}

/** Manages JWT token lifecycle: login, logout, persistence, and expiry checks. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = '/api/auth/login';
  private http = inject(HttpClient);
  private tokenKey = 'jwtToken';

  isLoggedIn = signal(false);
  token: string | null = null;

  constructor() {
    const storedToken = localStorage.getItem(this.tokenKey);
    if (storedToken && !this.isTokenExpired(storedToken)) {
      this.token = storedToken;
      this.isLoggedIn.set(true);
    } else {
      localStorage.removeItem(this.tokenKey);
    }
  }

  /**
   * Sends credentials to the backend and stores the JWT on success.
   *
   * @param userName username
   * @param password plain-text password
   */
  login(userName: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.apiUrl, { userName, password }).pipe(
      tap((response) => {
        if (response.success && response.token) {
          this.token = response.token;
          this.isLoggedIn.set(true);
          localStorage.setItem(this.tokenKey, response.token);
        }
      }),
    );
  }

  /** Removes token from memory and localStorage. Does NOT navigate. */
  logout(): void {
    this.token = null;
    this.isLoggedIn.set(false);
    localStorage.removeItem(this.tokenKey);
  }

  /** Returns HTTP headers with the Bearer token, or empty headers if not logged in. */
  getAuthHeaders(): HttpHeaders {
    if (this.token) {
      return new HttpHeaders({ Authorization: `Bearer ${this.token}` });
    }
    return new HttpHeaders();
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp * 1000;
      return Date.now() >= expiry;
    } catch {
      return true;
    }
  }
}
