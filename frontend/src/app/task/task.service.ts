import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from './task';

/** HTTP client for the {@code /api/tasks} endpoint. */
@Injectable({ providedIn: 'root' })
export class TaskService {
  private apiUrl = '/api/tasks';

  constructor(private http: HttpClient) {}

  /** Returns all tasks (no pagination). */
  getTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.apiUrl);
  }
}
