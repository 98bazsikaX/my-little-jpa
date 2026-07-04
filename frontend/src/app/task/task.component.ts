import { Component, signal, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { TaskService } from './task.service';
import { Task } from './task';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatListModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  template: `
    <mat-card>
      <mat-card-header>
        <mat-card-title>Tasks ({{ tasks().length }})</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        @if (loading()) {
          <div class="spinner-container">
            <mat-spinner diameter="40"></mat-spinner>
          </div>
        } @else if (tasks().length === 0) {
          <p>No tasks found.</p>
        } @else {
          <mat-list>
            @for (task of tasks(); track task.id) {
              <mat-list-item>
                <mat-icon matListItemIcon>{{
                  task.completed ? 'check_circle' : 'radio_button_unchecked'
                }}</mat-icon>
                <span matListItemTitle>{{ task.title }}</span>
              </mat-list-item>
            }
          </mat-list>
        }
      </mat-card-content>
    </mat-card>
  `,
  styles: [
    `
      .spinner-container {
        display: flex;
        justify-content: center;
        padding: 32px 0;
      }
    `,
  ],
})
export class TaskComponent implements OnInit {
  tasks = signal<Task[]>([]);
  loading = signal(true);

  private taskService = inject(TaskService);
  private snackBar = inject(MatSnackBar);

  ngOnInit(): void {
    this.taskService.getTasks().subscribe({
      next: (data) => {
        this.tasks.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.snackBar.open('Failed to load tasks', 'Close', { duration: 5000 });
      },
    });
  }
}
