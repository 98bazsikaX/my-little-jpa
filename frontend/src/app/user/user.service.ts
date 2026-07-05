import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from './user';

export interface CreateUserRequest {
  userName: string;
  email: string;
  firstName?: string;
  lastName?: string;
  password: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface UserFilter {
  userName?: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  created?: { from?: number; to?: number };
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = '/api/users';

  constructor(private http: HttpClient) {}

  getUsers(
    page: number,
    size: number,
    sort: string,
    order: string,
  ): Observable<PageResponse<User>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', `${sort},${order}`);
    return this.http.get<PageResponse<User>>(this.apiUrl, { params });
  }

  searchUsers(
    filter: UserFilter,
    page: number,
    size: number,
    sort: string,
    order: string,
  ): Observable<PageResponse<User>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', `${sort},${order}`);
    return this.http.post<PageResponse<User>>(`${this.apiUrl}/search`, filter, { params });
  }

  createUser(request: CreateUserRequest): Observable<User> {
    return this.http.post<User>(this.apiUrl, request);
  }
}
