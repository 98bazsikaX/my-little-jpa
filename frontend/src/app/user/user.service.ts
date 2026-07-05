import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from './user';

/** Request body for {@code POST /api/users}. */
export interface CreateUserRequest {
  userName: string;
  email: string;
  firstName?: string;
  lastName?: string;
  password: string;
}

/** Spring Data paginated response shape. */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

/** Optional filter criteria for {@code POST /api/users/search}. */
export interface UserFilter {
  userName?: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  created?: { from?: number; to?: number };
}

/** HTTP client for the {@code /api/users} endpoints. */
@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = '/api/users';

  constructor(private http: HttpClient) {}

  /**
   * Fetches a paginated page of all users.
   *
   * @param page  zero-based page index
   * @param size  page size
   * @param sort  sort column name
   * @param order sort direction ({@code asc} or {@code desc})
   */
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

  /**
   * Searches users with optional filter criteria.
   *
   * @param filter filter object (all fields optional)
   * @param page   zero-based page index
   * @param size   page size
   * @param sort   sort column name
   * @param order  sort direction ({@code asc} or {@code desc})
   */
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

  /**
   * Creates a new user.
   *
   * @param request user details including password
   */
  createUser(request: CreateUserRequest): Observable<User> {
    return this.http.post<User>(this.apiUrl, request);
  }
}
