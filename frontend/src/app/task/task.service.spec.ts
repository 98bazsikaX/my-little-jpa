import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { TaskService } from './task.service';
import { Task } from './task';

describe('TaskService', () => {
  let service: TaskService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    const http = TestBed.inject(HttpClient);
    service = new TaskService(http);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should fetch tasks from /api/tasks', () => {
    const mockTasks: Task[] = [{ id: 1, title: 'Test Task', completed: false }];

    service.getTasks().subscribe((tasks) => {
      expect(tasks).toEqual(mockTasks);
    });

    const req = httpTesting.expectOne('/api/tasks');
    expect(req.request.method).toBe('GET');
    req.flush(mockTasks);
  });

  it('should return empty array when no tasks', () => {
    service.getTasks().subscribe((tasks) => {
      expect(tasks).toEqual([]);
    });

    httpTesting.expectOne('/api/tasks').flush([]);
  });
});
