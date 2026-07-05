import { Component, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../auth.service';

/** Login page with username/password form. On success redirects to /tasks. */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  template: `
    <mat-card class="login-card">
      <mat-card-header>
        <mat-card-title>Login</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        <form (ngSubmit)="onSubmit()" #loginForm="ngForm">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Username</mat-label>
            <input matInput [(ngModel)]="userName" name="userName" required />
          </mat-form-field>
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Password</mat-label>
            <input matInput type="password" [(ngModel)]="password" name="password" required />
          </mat-form-field>
          @if (errorMessage()) {
            <p class="error">{{ errorMessage() }}</p>
          }
          <button
            mat-flat-button
            color="primary"
            type="submit"
            class="full-width"
            [disabled]="loading()"
          >
            @if (loading()) {
              <mat-spinner diameter="20"></mat-spinner>
            } @else {
              Login
            }
          </button>
        </form>
      </mat-card-content>
    </mat-card>
  `,
  styles: [
    `
      .login-card {
        max-width: 400px;
        margin: 48px auto;
      }
      .full-width {
        width: 100%;
        margin-bottom: 16px;
      }
      .error {
        color: #d32f2f;
        margin-bottom: 16px;
      }
    `,
  ],
})
export class LoginComponent {
  userName = '';
  password = '';
  loading = signal(false);
  errorMessage = signal('');

  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  onSubmit(): void {
    if (!this.userName || !this.password) return;

    this.loading.set(true);
    this.errorMessage.set('');

    this.authService.login(this.userName, this.password).subscribe({
      next: (response) => {
        this.loading.set(false);
        if (response.success) {
          this.router.navigate(['/tasks']);
        } else {
          this.errorMessage.set(response.message);
          this.snackBar.open(response.message, 'Close', { duration: 5000 });
        }
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Login failed. Please try again.';
        this.errorMessage.set(msg);
        this.snackBar.open(msg, 'Close', { duration: 5000 });
      },
    });
  }
}
