import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loginUrl = `${environment.apiUrl}/auth/login`;

  constructor(private http: HttpClient) {}

  login(identification: string, password: string): Observable<any> {
    const body = { identification, password };
    return this.http.post(this.loginUrl, body);
  }
}
