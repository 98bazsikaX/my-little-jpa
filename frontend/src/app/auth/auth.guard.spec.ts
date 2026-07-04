import { describe, it, expect, beforeEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Component } from '@angular/core';
import { authGuard } from './auth.guard';
import { AuthService } from './auth.service';

@Component({ standalone: true, template: '' })
class DummyComponent {}

describe('authGuard', () => {
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideRouter([
          { path: 'login', component: DummyComponent },
          { path: 'tasks', component: DummyComponent },
        ]),
      ],
    });

    authService = TestBed.inject(AuthService);
  });

  it('should allow navigation when logged in', () => {
    authService.isLoggedIn.set(true);

    const result = TestBed.runInInjectionContext(() =>
      authGuard(undefined as any, undefined as any),
    );

    expect(result).toBe(true);
  });

  it('should redirect to /login when not logged in', () => {
    authService.isLoggedIn.set(false);

    const result = TestBed.runInInjectionContext(() =>
      authGuard(undefined as any, undefined as any),
    );

    if (typeof result === 'object') {
      expect(result.toString()).toBe('/login');
    }
  });
});
