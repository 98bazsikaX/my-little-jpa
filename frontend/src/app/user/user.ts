export interface User {
  id: number;
  userName: string;
  email: string;
  firstName: string | null;
  lastName: string | null;
  lastLogin: number | null;
  created: number;
  updated: number | null;
}
