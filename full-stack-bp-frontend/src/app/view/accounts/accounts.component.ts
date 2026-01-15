import { Component, OnInit } from '@angular/core';
import { AccountService, Account } from '../../services/account.service';
import { NotificationService } from '../../services/notification.service';
import { ClientService } from '../../services/client.service';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.css']
})
export class AccountsComponent implements OnInit {
  displayedColumns: string[] = ['accountNumber', 'accountType', 'client', 'balance', 'status', 'actions'];
  accounts: Account[] = [];
  filteredAccounts: Account[] = [];
  clients: any[] = [];

  showForm = false;
  editing = false;
  currentAccount: Account = this.getEmptyAccount();

  searchTerm = '';
  statusFilter = '';
  clientFilter = '';

  isLoading = false;

  accountTypes = [
    { value: 'SAVINGS', label: 'Ahorros' },
    { value: 'CURRENT', label: 'Corriente' }
  ];

  constructor(
    private accountService: AccountService,
    private clientService: ClientService,
    private notification: NotificationService
  ) { }

  ngOnInit(): void {
    this.loadAccounts();
    this.loadClients();
  }

  loadAccounts(): void {
    this.isLoading = true;
    this.accountService.getAllAccounts().subscribe({
      next: (data) => {
        this.accounts = data;
        this.filteredAccounts = [...data];
      },
      complete: () => this.isLoading = false
    });
  }

  loadClients(): void {
    this.clientService.getAllClients().subscribe({
      next: (data) => {
        this.clients = data;
      }
    });
  }

  createAccount(): void {
    this.isLoading = true;
    const updatedAccount = { accountNumber: this.currentAccount.accountNumber, accountType: this.currentAccount.accountType, initialBalance: this.currentAccount.initialBalance, clientId: this.currentAccount.client.id, status: this.currentAccount.status };

    this.accountService.createAccount(updatedAccount).subscribe({
      next: () => {
        this.notification.showSuccess('Cuenta creada exitosamente');
        this.cancelForm();
        this.loadAccounts();
      },
      complete: () => this.isLoading = false
    });
  }

  updateAccount(): void {
    if (!this.currentAccount.id) return;

    this.isLoading = true;
    const updatedAccount = { accountNumber: this.currentAccount.accountNumber, accountType: this.currentAccount.accountType, initialBalance: this.currentAccount.initialBalance, clientId: this.currentAccount.client.id, status: this.currentAccount.status };

    this.accountService.updateAccount(this.currentAccount.id, updatedAccount).subscribe({
      next: () => {
        this.notification.showSuccess('Cuenta actualizada exitosamente');
        this.cancelForm();
        this.loadAccounts();
      },
      complete: () => this.isLoading = false
    });
  }

  deleteAccount(id: number, accountNumber: string): void {
    this.accountService.deleteAccount(id).subscribe({
      next: () => {
        this.notification.showSuccess('Cuenta eliminada exitosamente');
        this.loadAccounts();
      }
    });
  }

  toggleAccountStatus(account: Account): void {
    if (account.status) {
      this.accountService.deactivateAccount(account.id!).subscribe({
        next: () => {
          this.notification.showSuccess('Cuenta desactivada exitosamente');
          this.loadAccounts();
        }
      });
    } else {
      const updatedAccount = { accountNumber: account.accountNumber, accountType: account.accountType, initialBalance: account.initialBalance, clientId: account.client.id, status: true };
      this.accountService.updateAccount(account.id!, updatedAccount).subscribe({
        next: () => {
          this.notification.showSuccess('Cuenta activada exitosamente');
          this.loadAccounts();
        }
      });
    }
  }

  showCreateForm(): void {
    this.editing = false;
    this.currentAccount = this.getEmptyAccount();
    this.showForm = true;
  }

  showEditForm(account: Account): void {
    this.editing = true;
    this.currentAccount = { ...account };
    this.showForm = true;
  }

  cancelForm(): void {
    this.showForm = false;
    this.editing = false;
    this.currentAccount = this.getEmptyAccount();
  }

  onSubmit(): void {
    if (this.editing) {
      this.updateAccount();
    } else {
      this.createAccount();
    }
  }

  getEmptyAccount(): Account {
    return {
      accountNumber: '',
      accountType: 'SAVINGS',
      initialBalance: 0,
      status: true,
      client: { id: 0, name: '', identification: '' }
    };
  }

  applyFilter(): void {
    let filtered = this.accounts;

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(account =>
        account.accountNumber.toLowerCase().includes(term) ||
        account.client.name.toLowerCase().includes(term) ||
        account.client.identification.toLowerCase().includes(term)
      );
    }

    if (this.statusFilter !== '') {
      filtered = filtered.filter(account =>
        account.status.toString() === this.statusFilter
      );
    }

    if (this.clientFilter !== '') {
      filtered = filtered.filter(account =>
        account.client.id.toString() === this.clientFilter
      );
    }

    this.filteredAccounts = filtered;
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2
    }).format(amount);
  }

  getAccountTypeLabel(type: string): string {
    const found = this.accountTypes.find(t => t.value === type);
    return found ? found.label : type;
  }

}