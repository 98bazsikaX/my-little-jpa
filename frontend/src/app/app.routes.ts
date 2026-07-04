import { Routes } from '@angular/router';
import { TaskComponent } from './task/task.component';
import { LoginComponent } from './auth/login/login.component';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/tasks', pathMatch: 'full' },
  { path: 'tasks', component: TaskComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },
];
