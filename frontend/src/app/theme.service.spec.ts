import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { ThemeService } from './theme.service';

describe('ThemeService', () => {
  let service: ThemeService;

  beforeEach(() => {
    localStorage.clear();
    document.documentElement.removeAttribute('data-theme');
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({ providers: [ThemeService] });
    service = TestBed.inject(ThemeService);
  });

  afterEach(() => {
    document.documentElement.removeAttribute('data-theme');
  });

  it('should default to dark when no preference stored', () => {
    expect(service.theme()).toBe('dark');
    expect(document.documentElement.getAttribute('data-theme')).toBe('dark');
  });

  it('should toggle from dark to light', () => {
    service.toggle();
    expect(service.theme()).toBe('light');
    expect(localStorage.getItem('theme')).toBe('light');
    expect(document.documentElement.getAttribute('data-theme')).toBe('light');
  });

  it('should toggle from light to dark', () => {
    service.toggle();
    service.toggle();
    expect(service.theme()).toBe('dark');
    expect(localStorage.getItem('theme')).toBe('dark');
  });

  it('should persist each toggle to localStorage', () => {
    service.toggle();
    expect(localStorage.getItem('theme')).toBe('light');

    service.toggle();
    expect(localStorage.getItem('theme')).toBe('dark');
  });

  it('should apply data-theme attribute on html element', () => {
    service.toggle();
    expect(document.documentElement.getAttribute('data-theme')).toBe('light');

    service.toggle();
    expect(document.documentElement.getAttribute('data-theme')).toBe('dark');
  });
});
