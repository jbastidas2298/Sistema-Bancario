import { Component, OnInit } from '@angular/core';
import { ReportService, ReportResponse, TransactionDetail, AccountSummary } from '../../services/report.service';
import { ClientService } from '../../services/client.service';
import { NotificationService } from '../../services/notification.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  clients: any[] = [];
  selectedClientId: number | null = null;
  startDate: string = '';
  endDate: string = '';
  
  report: ReportResponse | null = null;
  isLoading = false;
  isGeneratingPdf = false;
  isPreviewingPdf = false;
  
  showAccounts = true;
  showTransactions = true;
  showSummary = true;
  
  transactionTypeFilter = '';
  accountFilter = '';
  
  pdfPreviewUrl: SafeResourceUrl | null = null;

  constructor(
    private reportService: ReportService,
    private clientService: ClientService,
    private notification: NotificationService,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit(): void {
    this.loadClients();
    this.setDefaultDates();
  }

  loadClients(): void {
    this.clientService.getAllClients().subscribe({
      next: (data) => {
        this.clients = data;
      }
    });
  }

  setDefaultDates(): void {
    const today = new Date();
    const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
    
    this.endDate = this.formatDateForInput(today);
    this.startDate = this.formatDateForInput(lastMonth);
  }

  formatDateForInput(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  generateReport(): void {
    if (!this.selectedClientId) {
      this.notification.showError('Seleccione un cliente');
      return;
    }

    if (!this.startDate || !this.endDate) {
      this.notification.showError('Seleccione un rango de fechas');
      return;
    }

    if (new Date(this.startDate) > new Date(this.endDate)) {
      this.notification.showError('La fecha de inicio debe ser anterior a la fecha de fin');
      return;
    }

    this.isLoading = true;
    this.report = null;

    this.reportService.generateReport(this.selectedClientId!, this.startDate, this.endDate).subscribe({
      next: (data) => {
        this.report = data;
        this.notification.showSuccess('Reporte generado exitosamente');
      },
      complete: () => this.isLoading = false
    });
  }

  generatePdfReport(): void {
    if (!this.selectedClientId) {
      this.notification.showError('Seleccione un cliente');
      return;
    }

    if (!this.startDate || !this.endDate) {
      this.notification.showError('Seleccione un rango de fechas');
      return;
    }

    this.isGeneratingPdf = true;

    this.reportService.generatePdfReport(this.selectedClientId!, this.startDate, this.endDate).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `estado-cuenta-${this.selectedClientId}-${this.startDate}-${this.endDate}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
        
        this.notification.showSuccess('PDF generado y descargado exitosamente');
      },
      complete: () => this.isGeneratingPdf = false
    });
  }

  previewPdfReport(): void {
    if (!this.selectedClientId) {
      this.notification.showError('Seleccione un cliente');
      return;
    }

    if (!this.startDate || !this.endDate) {
      this.notification.showError('Seleccione un rango de fechas');
      return;
    }

    this.isPreviewingPdf = true;

    this.reportService.generateBase64Report(this.selectedClientId!, this.startDate, this.endDate).subscribe({
      next: (base64String) => {
        const pdfUrl = `data:application/pdf;base64,${base64String}`;
        this.pdfPreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(pdfUrl);
        this.notification.showSuccess('Vista previa generada');
      },
      complete: () => this.isPreviewingPdf = false
    });
  }

  closePreview(): void {
    this.pdfPreviewUrl = null;
  }

  get filteredTransactions(): TransactionDetail[] {
    if (!this.report?.transactions) return [];

    let filtered = this.report.transactions;

    if (this.transactionTypeFilter) {
      filtered = filtered.filter(t => t.transactionType === this.transactionTypeFilter);
    }

    if (this.accountFilter) {
      filtered = filtered.filter(t => t.accountNumber === this.accountFilter);
    }

    return filtered;
  }

  get accountNumbers(): string[] {
    if (!this.report?.accounts) return [];
    return this.report.accounts.map(a => a.accountNumber);
  }

  formatCurrency(amount: number | null | undefined): string {
    const value = amount || 0;
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2
    }).format(value);
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    try {
      return new Date(dateString).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch {
      return dateString;
    }
  }

  formatDateTime(dateString: string): string {
    if (!dateString) return '';
    try {
      return new Date(dateString).toLocaleString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return dateString;
    }
  }

  getTransactionTypeLabel(type: string): string {
    switch(type) {
      case 'DEPOSIT': return 'Depósito';
      case 'WITHDRAWAL': return 'Retiro';
      case 'TRANSFER': return 'Transferencia';
      default: return type;
    }
  }

  getAccountTypeLabel(type: string): string {
    switch(type) {
      case 'SAVINGS': return 'Ahorros';
      case 'CHECKING': return 'Corriente';
      default: return type;
    }
  }

  getStatusLabel(status: boolean): string {
    return status ? 'Activa' : 'Inactiva';
  }

  getStatusClass(status: boolean): string {
    return status ? 'badge-active' : 'badge-inactive';
  }

  getTransactionTypeClass(type: string): string {
    switch(type) {
      case 'DEPOSIT': return 'badge-deposit';
      case 'WITHDRAWAL': return 'badge-withdrawal';
      case 'TRANSFER': return 'badge-transfer';
      default: return '';
    }
  }

  getAmountClass(type: string): string {
    switch(type) {
      case 'DEPOSIT': return 'amount-positive';
      case 'WITHDRAWAL': return 'amount-negative';
      case 'TRANSFER': return 'amount-transfer';
      default: return '';
    }
  }
}