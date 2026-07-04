import { Component, signal, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSortModule, MatSort, Sort } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UserService } from './user.service';
import { User } from './user';
import { UserDialogComponent } from './user-dialog.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialogModule,
  ],
  templateUrl: './user.component.html',
  styles: [
    `
      .spinner-container {
        display: flex;
        justify-content: center;
        padding: 32px 0;
      }
      .table-container {
        overflow-x: auto;
      }
      table {
        width: 100%;
      }
    `,
  ],
})
export class UserComponent implements OnInit {
  users = signal<User[]>([]);
  loading = signal(true);
  totalElements = signal(0);

  displayedColumns = ['userName', 'email', 'firstName', 'lastName', 'created'];

  pageSize = 10;
  pageIndex = 0;
  sortActive = 'userName';
  sortDirection: 'asc' | 'desc' = 'asc';

  private userService = inject(UserService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.userService
      .getUsers(this.pageIndex, this.pageSize, this.sortActive, this.sortDirection)
      .subscribe({
        next: (page) => {
          this.users.set(page.content);
          this.totalElements.set(page.totalElements);
          this.loading.set(false);
        },
        error: (err) => {
          this.loading.set(false);
          this.snackBar.open(err.error?.message || 'Failed to load users', 'Close', {
            duration: 5000,
          });
        },
      });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadUsers();
  }

  onSortChange(sort: Sort): void {
    this.sortActive = sort.active || 'userName';
    this.sortDirection = sort.direction === 'desc' ? 'desc' : 'asc';
    this.pageIndex = 0;
    this.loadUsers();
  }

  openCreateDialog(): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadUsers();
      }
    });
  }
}
