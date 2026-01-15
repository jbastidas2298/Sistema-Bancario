import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoadingService } from './loading.service';
import { NotificationService } from './notification.service';
import { Router } from '@angular/router';
import { catchError, finalize, map, Observable, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable()
export class InterceptorService implements HttpInterceptor {
  constructor(
    private loadingService: LoadingService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('token');
    const loginUrl = `${environment.apiUrl}/api/v1/auth/login`;

    if (token) {
      const formattedToken = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
      if (request.url !== loginUrl) {
        request = request.clone({
          setHeaders: {
            Authorization: formattedToken,
          },
        });
      }
    }

    this.loadingService.show();

    return next.handle(request).pipe(
      map((event: HttpEvent<any>) => {
        if (
          event instanceof HttpResponse &&
          event.headers.get('content-type')?.includes('text/plain')
        ) {
          return event.clone({ body: event.body });
        }
        return event;
      }),
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Ha ocurrido un error inesperado. Por favor, intenta de nuevo.';

        if (error.status === 401) {
          localStorage.removeItem('token');
          this.router.navigate(['/login']);
          errorMessage = 'Tu sesión ha expirado. Inicia sesión nuevamente.';
        } else if (error.error) {
          if (typeof error.error === 'string') {
            try {
              const parsedError = JSON.parse(error.error);
              if (parsedError.message) {
                errorMessage = `Código del error: ${parsedError.code || 'Desconocido'}. Mensaje: ${parsedError.message}`;
              }
            } catch (e) {
              errorMessage = error.error;
            }
          } else if (typeof error.error === 'object' && error.error.message) {
            const backendError = error.error as { code: string; message: string };
            errorMessage = `Código del error: ${backendError.code || 'Desconocido'}. Mensaje: ${backendError.message}`;
          }
        }
        this.notificationService.showError(errorMessage);

        return throwError(() => error);
      }),
      finalize(() => this.loadingService.hide())
    );
  }
}
