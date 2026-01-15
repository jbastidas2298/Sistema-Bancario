import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Account {
  id?: number;
  accountNumber: string;
  accountType: string;
  initialBalance: number;
  currentBalance?: number;
  status: boolean;
  client: {
    id: number;
    name: string;
    identification: string;
  };
}

export interface AccountRequest {
  accountNumber: string;
  accountType: string;
  initialBalance: number;
  clientId: number;
  status: boolean;
}



@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = `${environment.apiUrl}/accounts`;

  constructor(private http: HttpClient) { }

  getAllAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }

  getAccountById(id: number): Observable<Account> {
    return this.http.get<Account>(`${this.apiUrl}/${id}`);
  }

  getAccountsByClient(clientId: number): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.apiUrl}/client/${clientId}`);
  }

  createAccount(account: AccountRequest): Observable<Account> {
    return this.http.post<Account>(this.apiUrl, account);
  }

  updateAccount(id: number, account: AccountRequest): Observable<Account> {
    return this.http.put<Account>(`${this.apiUrl}/${id}`, account);
  }

  deactivateAccount(id: number): Observable<Account> {
    return this.http.patch<Account>(`${this.apiUrl}/${id}/deactivate`, {});
  }

  deleteAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAccountBalance(accountNumber: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/balance/${accountNumber}`);
  }
}