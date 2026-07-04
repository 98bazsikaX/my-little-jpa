import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { Component } from '@angular/core';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';

@Component({ standalone: true, template: '' })
class DummyComponent {}

describe('authInterceptor', () => {
  let httpClient: HttpClient;
  let httpTesting: HttpTestingController;
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        provideRouter([{ path: 'login', component: DummyComponent }]),
      ],
    });

    httpClient = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should add Authorization header when token exists', () => {
    authService.token = 'test.token';

    httpClient.get('/api/tasks').subscribe();

    const req = httpTesting.expectOne('/api/tasks');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test.token');
    req.flush([]);
  });

  it('should not add Authorization header when no token', () => {
    authService.token = null;

    httpClient.get('/api/tasks').subscribe();

    const req = httpTesting.expectOne('/api/tasks');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush([]);
  });

  it('should redirect to login on 401 response', () => {
    const router = TestBed.inject(Router);
    const navigateSpy = vi.spyOn(router, 'navigate');

    authService.token = 'expired.token';

    httpClient.get('/api/tasks').subscribe({
      error: () => {
        expect(authService.isLoggedIn()).toBe(false);
      },
    });

    httpTesting
      .expectOne('/api/tasks')
      .flush({ error: 'Unauthorized' }, { status: 401, statusText: 'Unauthorized' });

    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should not add header to absolute URLs', () => {
    authService.token = 'test.token';

    httpClient.get('https://external.api/data').subscribe();

    const req = httpTesting.expectOne('https://external.api/data');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush([]);
  });
});
