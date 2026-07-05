import { Component, signal, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UserService, UserFilter } from './user.service';
import { User } from './user';
import { UserDialogComponent } from './user-dialog.component';
import { debounceTime, Subject } from 'rxjs';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
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
      .filter-field {
        width: 100%;
      }
      .filter-field ::ng-deep .mat-mdc-form-field-subscript-wrapper {
        display: none;
      }
    `,
  ],
})
export class UserComponent implements OnInit {
  users = signal<User[]>([]);
  loading = signal(true);
  totalElements = signal(0);

  displayedColumns = ['userName', 'email', 'firstName', 'lastName', 'created'];
  filterColumns = [
    'filter-userName',
    'filter-email',
    'filter-firstName',
    'filter-lastName',
    'filter-created',
  ];

  pageSize = 10;
  pageIndex = 0;
  sortActive = 'userName';
  sortDirection: 'asc' | 'desc' = 'asc';

  filterUserName = signal('');
  filterEmail = signal('');
  filterFirstName = signal('');
  filterLastName = signal('');
  filterCreatedStart = signal<Date | null>(null);
  filterCreatedEnd = signal<Date | null>(null);

  private search$ = new Subject<void>();
  private userService = inject(UserService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  constructor() {
    this.search$.pipe(debounceTime(300)).subscribe(() => {
      this.pageIndex = 0;
      this.loadUsers();
    });
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  get hasFilters(): boolean {
    return (
      !!this.filterUserName() ||
      !!this.filterEmail() ||
      !!this.filterFirstName() ||
      !!this.filterLastName() ||
      !!this.filterCreatedStart() ||
      !!this.filterCreatedEnd()
    );
  }

  buildFilter(): UserFilter {
    const filter: UserFilter = {};
    const u = this.filterUserName().trim();
    const e = this.filterEmail().trim();
    const f = this.filterFirstName().trim();
    const l = this.filterLastName().trim();
    const cStart = this.filterCreatedStart();
    const cEnd = this.filterCreatedEnd();

    if (u) filter.userName = u;
    if (e) filter.email = e;
    if (f) filter.firstName = f;
    if (l) filter.lastName = l;
    if (cStart || cEnd) {
      filter.created = {};
      if (cStart)
        filter.created.from = Date.UTC(
          cStart.getFullYear(),
          cStart.getMonth(),
          cStart.getDate(),
        );
      if (cEnd)
        filter.created.to = Date.UTC(
          cEnd.getFullYear(),
          cEnd.getMonth(),
          cEnd.getDate(),
        );
    }
    return filter;
  }

  loadUsers(): void {
    this.loading.set(true);

    if (this.hasFilters) {
      const filter = this.buildFilter();
      this.userService
        .searchUsers(filter, this.pageIndex, this.pageSize, this.sortActive, this.sortDirection)
        .subscribe({
          next: (page) => this.handlePage(page),
          error: (err) => this.handleError(err),
        });
    } else {
      this.userService
        .getUsers(this.pageIndex, this.pageSize, this.sortActive, this.sortDirection)
        .subscribe({
          next: (page) => this.handlePage(page),
          error: (err) => this.handleError(err),
        });
    }
  }

  clearFilters(): void {
    this.filterUserName.set('');
    this.filterEmail.set('');
    this.filterFirstName.set('');
    this.filterLastName.set('');
    this.filterCreatedStart.set(null);
    this.filterCreatedEnd.set(null);
    this.loadUsers();
  }

  onFilterChange(): void {
    this.search$.next();
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
    const dialogRef = this.dialog.open(UserDialogComponent, { width: '400px' });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) this.loadUsers();
    });
  }

  private handlePage(page: { content: User[]; totalElements: number }): void {
    this.users.set(page.content);
    this.totalElements.set(page.totalElements);
    this.loading.set(false);
  }

  private handleError(err: any): void {
    this.loading.set(false);
    this.snackBar.open(err.error?.message || 'Failed to load users', 'Close', { duration: 5000 });
  }
}
