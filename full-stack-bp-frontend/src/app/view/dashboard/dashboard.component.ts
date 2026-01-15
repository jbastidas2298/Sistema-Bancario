import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ClientService } from '../../services/client.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  currentUser: string = '';
  
  constructor(
    private authService: AuthService,
    private clientService: ClientService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.clientService.getUsername();
  }

  logout(): void {
    localStorage.clear();
    this.notificationService.showSuccess('Sesión cerrada exitosamente');
    this.router.navigate(['/login']);
  }
}