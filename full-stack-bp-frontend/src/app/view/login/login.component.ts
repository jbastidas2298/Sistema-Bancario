import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { AuthService } from '../../services/auth.service';
import { ClientService } from '../../services/client.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  hide = true; 
  
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService,
    private clientService: ClientService
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onLogin() {
    if (this.loginForm.valid) {
      const { username, password } = this.loginForm.value;
  
      this.authService.login(username, password).subscribe({
        next: (response) => {
          if (response && response.token) {
            localStorage.setItem('token', response.token);
  
            const decodedToken: any = jwtDecode(response.token);
            const roles = decodedToken.roles || [];
            const usuario = decodedToken.sub || decodedToken.username;
  
            this.clientService.setUserDetails(roles, usuario);
  
            this.notificationService.showSuccess('Inicio de sesión exitoso');
            this.router.navigate(['/clients']);
          }
        },
      });
    }
  }
}