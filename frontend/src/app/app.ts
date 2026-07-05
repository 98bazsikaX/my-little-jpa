import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from './auth/auth.service';
import { ThemeService } from './theme.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
/** Root component with navigation toolbar, logout button, and theme toggle. */
export class AppComponent {
  authService = inject(AuthService);
  themeService = inject(ThemeService);
  private router = inject(Router);

  /** Clears JWT token and redirects to the login page. */
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  /** Toggles between light and dark theme. */
  toggleTheme(): void {
    this.themeService.toggle();
  }
}
