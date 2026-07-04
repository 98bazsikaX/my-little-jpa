import { Component, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UserService, CreateUserRequest } from './user.service';

@Component({
  selector: 'app-user-dialog',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
  ],
  template: `
    <h2 mat-dialog-title>New User</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Username</mat-label>
        <input matInput [(ngModel)]="userName" name="userName" required />
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Email</mat-label>
        <input matInput type="email" [(ngModel)]="email" name="email" required />
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>First Name</mat-label>
        <input matInput [(ngModel)]="firstName" name="firstName" />
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Last Name</mat-label>
        <input matInput [(ngModel)]="lastName" name="lastName" />
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Password</mat-label>
        <input matInput type="password" [(ngModel)]="password" name="password" required />
      </mat-form-field>
      @if (error()) {
        <p class="error">{{ error() }}</p>
      }
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-flat-button color="primary" (click)="create()" [disabled]="loading()">
        @if (loading()) {
          <mat-spinner diameter="20"></mat-spinner>
        } @else {
          Create
        }
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      .full-width {
        width: 100%;
      }
      .error {
        color: #d32f2f;
      }
    `,
  ],
})
export class UserDialogComponent {
  userName = '';
  email = '';
  firstName = '';
  lastName = '';
  password = '';

  loading = signal(false);
  error = signal('');

  private userService = inject(UserService);
  private dialogRef = inject(MatDialogRef<UserDialogComponent>);

  create(): void {
    if (!this.userName || !this.email || !this.password) return;

    this.loading.set(true);
    this.error.set('');

    const request: CreateUserRequest = {
      userName: this.userName,
      email: this.email,
      firstName: this.firstName || undefined,
      lastName: this.lastName || undefined,
      password: this.password,
    };

    this.userService.createUser(request).subscribe({
      next: (user) => {
        this.dialogRef.close(user);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err.error?.message || 'Failed to create user');
      },
    });
  }
}
