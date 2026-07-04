export interface User {
  id: number;
  userName: string;
  email: string;
  firstName: string | null;
  lastName: string | null;
  lastLogin: string | null;
  created: string;
  updated: string | null;
}
