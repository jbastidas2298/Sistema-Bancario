import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ReportResponse {
  clientName: string;
  clientIdentification: string;
  startDate: string;
  endDate: string;
  accounts: AccountSummary[];
  totalDeposits: number;
  totalWithdrawals: number;
  totalTransfers: number;
  transactions: TransactionDetail[];
}

export interface AccountSummary {
  accountNumber: string;
  accountType: string;
  initialBalance: number;
  currentBalance: number;
  status: boolean;
}

export interface TransactionDetail {
  date: string;
  accountNumber: string;
  transactionType: string;
  amount: number;
  balance: number;
  description: string;
  destinationAccountNumber?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private apiUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) { }

  generateReport(clientId: number, startDate: string, endDate: string): Observable<ReportResponse> {
    const params = new HttpParams()
      .set('clientId', clientId.toString())
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<ReportResponse>(this.apiUrl, { params });
  }

  generatePdfReport(clientId: number, startDate: string, endDate: string): Observable<Blob> {
    const params = new HttpParams()
      .set('clientId', clientId.toString())
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get(`${this.apiUrl}/pdf`, {
      params,
      responseType: 'blob'
    });
  }

  generateBase64Report(clientId: number, startDate: string, endDate: string): Observable<string> {
    const params = new HttpParams()
      .set('clientId', clientId.toString())
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get(`${this.apiUrl}/base64`, {
      params,
      responseType: 'text'
    });
  }
}