import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [AuthService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(AuthService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should start with isLoggedIn false when no token stored', () => {
    expect(service.isLoggedIn()).toBe(false);
    expect(service.token).toBeNull();
  });

  it('should set isLoggedIn and store token on successful login', () => {
    service.login('admin', '1234').subscribe((response) => {
      expect(response.success).toBe(true);
    });

    const req = httpTesting.expectOne('/api/auth/login');
    expect(req.request.body).toEqual({ userName: 'admin', password: '1234' });
    req.flush({ success: true, message: 'ok', token: 'test.jwt.token' });

    expect(service.isLoggedIn()).toBe(true);
    expect(service.token).toBe('test.jwt.token');
    expect(localStorage.getItem('jwtToken')).toBe('test.jwt.token');
  });

  it('should not set isLoggedIn on failed login', () => {
    service.login('admin', 'wrong').subscribe((response) => {
      expect(response.success).toBe(false);
    });

    httpTesting.expectOne('/api/auth/login').flush({
      success: false,
      message: 'Invalid credentials',
    });

    expect(service.isLoggedIn()).toBe(false);
    expect(service.token).toBeNull();
  });

  it('should clear state on logout', () => {
    localStorage.setItem('jwtToken', 'some.token');
    const freshService = TestBed.inject(AuthService);
    expect(freshService.isLoggedIn()).toBe(false);

    freshService.token = 'active.token';
    freshService.isLoggedIn.set(true);
    freshService.logout();

    expect(freshService.isLoggedIn()).toBe(false);
    expect(freshService.token).toBeNull();
    expect(localStorage.getItem('jwtToken')).toBeNull();
  });

  it('should return auth headers when token exists', () => {
    service.token = 'test.token';

    const headers = service.getAuthHeaders();
    expect(headers.get('Authorization')).toBe('Bearer test.token');
  });

  it('should return empty headers when no token', () => {
    const headers = service.getAuthHeaders();
    expect(headers.get('Authorization')).toBeNull();
  });
});
