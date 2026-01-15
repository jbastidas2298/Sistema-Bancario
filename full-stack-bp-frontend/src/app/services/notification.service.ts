import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationContainer: HTMLElement | null = null;

  constructor() {
    this.initializeNotificationContainer();
  }

  private initializeNotificationContainer(): void {
    this.notificationContainer = document.createElement('div');
    this.notificationContainer.id = 'notification-container';
    this.notificationContainer.style.cssText = `
      position: fixed;
      top: 20px;
      left: 50%;
      transform: translateX(-50%);
      z-index: 9999;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;
      width: 100%;
      max-width: 400px;
      pointer-events: none;
    `;
    document.body.appendChild(this.notificationContainer);
  }

  private createNotification(message: string, type: 'error' | 'success'): HTMLElement {
    const notification = document.createElement('div');
    const isError = type === 'error';
    
    notification.textContent = message;
    notification.style.cssText = `
      background-color: ${isError ? '#f44336' : '#4caf50'};
      color: white;
      padding: 16px 24px;
      border-radius: 4px;
      box-shadow: 0 3px 5px rgba(0,0,0,0.2);
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      font-size: 14px;
      text-align: center;
      min-width: 200px;
      max-width: 100%;
      opacity: 0;
      transform: translateY(-20px);
      transition: opacity 0.3s ease, transform 0.3s ease;
      pointer-events: auto;
      cursor: pointer;
    `;

    setTimeout(() => {
      notification.style.opacity = '1';
      notification.style.transform = 'translateY(0)';
    }, 10);

    setTimeout(() => {
      this.removeNotification(notification);
    }, 5000);

    notification.addEventListener('click', () => {
      this.removeNotification(notification);
    });

    return notification;
  }

  private removeNotification(notification: HTMLElement): void {
    notification.style.opacity = '0';
    notification.style.transform = 'translateY(-20px)';
    setTimeout(() => {
      if (notification.parentNode && this.notificationContainer) {
        this.notificationContainer.removeChild(notification);
      }
    }, 300);
  }

  showError(message: string): void {
    if (!this.notificationContainer) return;
    const notification = this.createNotification(message, 'error');
    this.notificationContainer.appendChild(notification);
  }

  showSuccess(message: string): void {
    if (!this.notificationContainer) return;
    const notification = this.createNotification(message, 'success');
    this.notificationContainer.appendChild(notification);
  }

  ngOnDestroy(): void {
    if (this.notificationContainer && this.notificationContainer.parentNode) {
      document.body.removeChild(this.notificationContainer);
    }
  }
}