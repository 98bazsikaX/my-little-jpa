/** Shape of a user returned by the API. Date fields are UTC epoch milliseconds. */
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
