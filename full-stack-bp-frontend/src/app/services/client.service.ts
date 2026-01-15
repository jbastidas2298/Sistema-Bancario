import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Client {
  id?: number;
  name: string;
  gender: string;
  age: number;
  identification: string;
  address: string;
  phone: string;
  password: string;
  status: boolean;
  clientId?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private apiUrl = `${environment.apiUrl}/clients`;
  private readonly USERNAME_KEY = 'username';

  constructor(private http: HttpClient) { }

  setUserDetails(roles: string[], username: string) {
    localStorage.setItem(this.USERNAME_KEY, username);
  }

  getUsername(): string {
    return localStorage.getItem(this.USERNAME_KEY) || '';
  }

  clearUserDetails() {
    localStorage.removeItem(this.USERNAME_KEY);
  }

  createClient(client: Client): Observable<Client> {
    return this.http.post<Client>(this.apiUrl, client);
  }

  getClientById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.apiUrl}/${id}`);
  }

  getClientByIdentification(identification: string): Observable<Client> {
    return this.http.get<Client>(`${this.apiUrl}/identification/${identification}`);
  }

  getAllClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.apiUrl);
  }

  getActiveClients(): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.apiUrl}/active`);
  }

  updateClient(id: number, client: Client): Observable<Client> {
    return this.http.put<Client>(`${this.apiUrl}/${id}`, client);
  }

  deactivateClient(id: number): Observable<Client> {
    return this.http.patch<Client>(`${this.apiUrl}/${id}/deactivate`, {});
  }

  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchClientsByName(name: string): Observable<Client[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<Client[]>(`${this.apiUrl}/search`, { params });
  }
}