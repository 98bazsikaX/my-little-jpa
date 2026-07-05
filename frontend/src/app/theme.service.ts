import { Injectable, signal, inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';

export type Theme = 'light' | 'dark';

/**
 * Manages light/dark theme toggle. Persists preference to localStorage and
 * falls back to the OS {@code prefers-color-scheme} media query.
 */
@Injectable({ providedIn: 'root' })
export class ThemeService {
  private document = inject(DOCUMENT);
  private storageKey = 'theme';

  theme = signal<Theme>(this.loadTheme());

  constructor() {
    this.apply(this.theme());
  }

  toggle(): void {
    const next = this.theme() === 'dark' ? 'light' : 'dark';
    this.theme.set(next);
    localStorage.setItem(this.storageKey, next);
    this.apply(next);
  }

  private loadTheme(): Theme {
    const stored = localStorage.getItem(this.storageKey) as Theme | null;
    if (stored === 'dark' || stored === 'light') return stored;

    if (window.matchMedia?.('(prefers-color-scheme: light)').matches) {
      return 'light';
    }
    return 'dark';
  }

  private apply(theme: Theme): void {
    this.document.documentElement.setAttribute('data-theme', theme);
  }
}
