import { Component, OnInit } from '@angular/core';
import { ClientService, Client } from '../../services/client.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-clients',
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.css']
})
export class ClientsComponent implements OnInit {
  clients: Client[] = [];
  filteredClients: Client[] = [];

  showForm = false;
  editing = false;
  currentClient: Client = this.getEmptyClient();

  searchTerm = '';
  statusFilter = '';

  pageSize = 10;
  currentPage = 0;
  totalClients = 0;

  isLoading = false;

  constructor(
    private clientService: ClientService,
    private notificacion: NotificationService
  ) { }

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.isLoading = true;
    this.clientService.getAllClients().subscribe({
      next: (data) => {
        this.clients = data;
        this.applyFilter();
        this.isLoading = false;
      }
    });
  }

  createClient(): void {
    this.isLoading = true;
    this.clientService.createClient(this.currentClient).subscribe({
      next: () => {
        this.notificacion.showSuccess('Cliente creado exitosamente');
        this.cancelForm();
        this.loadClients();
      }
    });
  }

  updateClient(): void {
    if (!this.currentClient.id) return;

    this.isLoading = true;
    this.clientService.updateClient(this.currentClient.id, this.currentClient).subscribe({
      next: () => {
        this.notificacion.showSuccess('Cliente actualizado exitosamente');
        this.cancelForm();
        this.loadClients();
      }
    });
  }

  deleteClient(id: number, name: string): void {
    this.clientService.deleteClient(id).subscribe({
      next: () => {
        this.notificacion.showSuccess('Cliente eliminado exitosamente');
        this.loadClients();
      }
    });
  }

  toggleClientStatus(client: Client): void {
    if (client.status) {
      this.clientService.deactivateClient(client.id!).subscribe({
        next: () => {
          this.notificacion.showSuccess('Cliente desactivado exitosamente');
          this.loadClients();
        }
      });
    } else {
      const updatedClient = { ...client, status: true };
      this.clientService.updateClient(client.id!, updatedClient).subscribe({
        next: () => {
          this.notificacion.showSuccess('Cliente activado exitosamente');
          this.loadClients();
        }
      });
    }
  }

  showCreateForm(): void {
    this.editing = false;
    this.currentClient = this.getEmptyClient();
    this.showForm = true;
  }

  showEditForm(client: Client): void {
    this.editing = true;
    this.currentClient = { ...client };
    this.showForm = true;
  }

  cancelForm(): void {
    this.showForm = false;
    this.editing = false;
    this.currentClient = this.getEmptyClient();
  }

  onSubmit(): void {
    if (this.editing) {
      this.updateClient();
    } else {
      this.createClient();
    }
  }

  getEmptyClient(): Client {
    return {
      name: '',
      gender: 'M',
      age: 18,
      identification: '',
      address: '',
      phone: '',
      password: '',
      status: true
    };
  }

  applyFilter(): void {
    let filtered = this.clients;

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(client =>
        client.name.toLowerCase().includes(term) ||
        client.identification.toLowerCase().includes(term) ||
        client.phone.toLowerCase().includes(term)
      );
    }

    if (this.statusFilter !== '') {
      filtered = filtered.filter(client =>
        client.status.toString() === this.statusFilter
      );
    }

    this.filteredClients = filtered;
    this.totalClients = filtered.length;
    this.currentPage = 0;
  }

  get paginatedClients(): Client[] {
    const startIndex = this.currentPage * this.pageSize;
    return this.filteredClients.slice(startIndex, startIndex + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.totalClients / this.pageSize);
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
    }
  }

  formatPhone(phone: string): string {
    if (!phone) return '';
    if (phone.length === 10) {
      return `${phone.substring(0, 3)}-${phone.substring(3, 6)}-${phone.substring(6)}`;
    }
    return phone;
  }
}