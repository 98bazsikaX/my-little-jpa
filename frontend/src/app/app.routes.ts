import { Routes } from '@angular/router';
import { TaskComponent } from './task/task.component';
import { UserComponent } from './user/user.component';
import { LoginComponent } from './auth/login/login.component';
import { authGuard } from './auth/auth.guard';

/** Application route table. Tasks and users are guarded; login is public. */
export const routes: Routes = [
  { path: '', redirectTo: '/tasks', pathMatch: 'full' },
  { path: 'tasks', component: TaskComponent, canActivate: [authGuard] },
  { path: 'users', component: UserComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },
];
