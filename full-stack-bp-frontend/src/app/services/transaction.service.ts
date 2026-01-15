import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Transaction {
  id?: number;
  date: string;
  transactionType: string;
  amount: number;
  balance: number;
  description: string;
  account: {
    id: number;
    accountNumber: string;
    accountType: string;
    client: {
      id: number;
      name: string;
      identification: string;
    } | null;
  };
}

export interface TransactionRequest {
  accountNumber: string;
  transactionType: string;
  amount: number;
  description: string;
  destinationAccountNumber?: string;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = `${environment.apiUrl}/transactions`;

  constructor(private http: HttpClient) { }

  createTransaction(request: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(this.apiUrl, request);
  }

  deposit(request: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.apiUrl}/deposit`, request);
  }

  withdraw(request: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.apiUrl}/withdraw`, request);
  }

  transfer(request: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.apiUrl}/transfer`, request);
  }

  getAllTransactionsByAccount(accountNumber: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.apiUrl}/account/${accountNumber}`);
  }

  getTransactionById(id: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.apiUrl}/${id}`);
  }

  getTransactionsByClientAndDateRange(
    clientId: number,
    startDate: string,
    endDate: string
  ): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(
      `${this.apiUrl}/client/${clientId}?startDate=${startDate}&endDate=${endDate}`
    );
  }
}