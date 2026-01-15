import { Component, OnInit } from '@angular/core';
import { TransactionService, Transaction, TransactionRequest } from '../../services/transaction.service';
import { AccountService } from '../../services/account.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css']
})
export class TransactionsComponent implements OnInit {
  displayedColumns: string[] = ['date', 'type', 'account', 'amount', 'balance', 'description'];
  transactions: Transaction[] = [];
  filteredTransactions: Transaction[] = [];
  accounts: any[] = [];

  showDepositForm = false;
  showWithdrawForm = false;
  showTransferForm = false;

  currentTransaction: TransactionRequest = this.getEmptyTransaction();

  searchTerm = '';
  accountFilter = '';
  startDate = '';
  endDate = '';

  isLoading = false;

  transactionTypes = [
    { value: 'DEPOSIT', label: 'Depósito' },
    { value: 'WITHDRAWAL', label: 'Retiro' },
    { value: 'TRANSFER', label: 'Transferencia' }
  ];

  constructor(
    private transactionService: TransactionService,
    private accountService: AccountService,
    private notification: NotificationService
  ) { }

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.accountService.getAllAccounts().subscribe({
      next: (data) => {
        this.accounts = data;
      }
    });
  }

  loadTransactionsByAccount(accountNumber: string): void {
    if (!accountNumber) return;

    this.isLoading = true;
    this.transactionService.getAllTransactionsByAccount(accountNumber).subscribe({
      next: (data) => {
        this.transactions = data;
        this.filteredTransactions = [...data];
        this.transactions = data;
      },
      complete: () => this.isLoading = false
    });
  }

  createDeposit(): void {
    if (!this.validateTransaction()) return;

    this.isLoading = true;
    this.transactionService.deposit(this.currentTransaction).subscribe({
      next: () => {
        this.notification.showSuccess('Depósito realizado exitosamente');
        this.resetForm();
        this.loadTransactionsByAccount(this.currentTransaction.accountNumber);
      },
      complete: () => this.isLoading = false
    });
  }

  createWithdrawal(): void {
    if (!this.validateTransaction()) return;

    this.isLoading = true;
    this.transactionService.withdraw(this.currentTransaction).subscribe({
      next: () => {
        this.notification.showSuccess('Retiro realizado exitosamente');
        this.resetForm();
        this.loadTransactionsByAccount(this.currentTransaction.accountNumber);
      },
      complete: () => this.isLoading = false
    });
  }

  createTransfer(): void {
    if (!this.validateTransfer()) return;

    this.isLoading = true;
    this.transactionService.transfer(this.currentTransaction).subscribe({
      next: () => {
        this.notification.showSuccess('Transferencia realizada exitosamente');
        this.resetForm();
        this.loadTransactionsByAccount(this.currentTransaction.accountNumber);
      },
      complete: () => this.isLoading = false
    });
  }

  validateTransaction(): boolean {
    if (!this.currentTransaction.accountNumber) {
      this.notification.showError('Seleccione una cuenta');
      return false;
    }
    if (!this.currentTransaction.amount || this.currentTransaction.amount <= 0) {
      this.notification.showError('Ingrese un monto válido');
      return false;
    }
    if (!this.currentTransaction.description) {
      this.notification.showError('Ingrese una descripción');
      return false;
    }
    return true;
  }

  validateTransfer(): boolean {
    if (!this.validateTransaction()) return false;

    if (!this.currentTransaction.destinationAccountNumber) {
      this.notification.showError('Ingrese la cuenta destino');
      return false;
    }

    if (this.currentTransaction.accountNumber === this.currentTransaction.destinationAccountNumber) {
      this.notification.showError('No puede transferir a la misma cuenta');
      return false;
    }

    return true;
  }

  showDeposit(): void {
    this.resetForms();
    this.showDepositForm = true;
    this.currentTransaction.transactionType = 'DEPOSIT';
  }

  showWithdrawal(): void {
    this.resetForms();
    this.showWithdrawForm = true;
    this.currentTransaction.transactionType = 'WITHDRAWAL';
  }

  showTransfer(): void {
    this.resetForms();
    this.showTransferForm = true;
    this.currentTransaction.transactionType = 'TRANSFER';
  }

  resetForm(): void {
    this.currentTransaction = this.getEmptyTransaction();
    this.resetForms();
  }

  resetForms(): void {
    this.showDepositForm = false;
    this.showWithdrawForm = false;
    this.showTransferForm = false;
  }

  getEmptyTransaction(): TransactionRequest {
    return {
      accountNumber: '',
      transactionType: 'DEPOSIT',
      amount: 0,
      description: '',
      destinationAccountNumber: ''
    };
  }

  applyFilter(): void {
    let filtered = this.transactions;

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(transaction => {
        const account = transaction.account;
        const clientName = account.client?.name || '';
        const clientId = account.client?.identification || '';

        return (
          transaction.description.toLowerCase().includes(term) ||
          account.accountNumber.toLowerCase().includes(term) ||
          clientName.toLowerCase().includes(term) ||
          clientId.toLowerCase().includes(term)
        );
      });
    }

    if (this.accountFilter) {
      filtered = filtered.filter(transaction =>
        transaction.account.accountNumber === this.accountFilter
      );
    }

    this.filteredTransactions = filtered;
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
      return new Date(dateString).toLocaleString('es-ES');
    } catch {
      return dateString;
    }
  }

  getTransactionTypeClass(type: string): string {
    switch (type) {
      case 'DEPOSIT': return 'badge-deposit';
      case 'WITHDRAWAL': return 'badge-withdrawal';
      case 'TRANSFER': return 'badge-transfer';
      default: return '';
    }
  }

  getTransactionTypeLabel(type: string): string {
    switch (type) {
      case 'DEPOSIT': return 'Depósito';
      case 'WITHDRAWAL': return 'Retiro';
      case 'TRANSFER': return 'Transferencia';
      default: return type;
    }
  }

  getClientInfo(transaction: Transaction): string {
    const client = transaction.account.client;
    if (!client) return transaction.account.accountNumber;
    return `${transaction.account.accountNumber} - ${client.name}`;
  }

  getAmountClass = (type: string): string => {
    switch (type) {
      case 'DEPOSIT': return 'amount-positive';
      case 'WITHDRAWAL': return 'amount-negative';
      case 'TRANSFER': return 'amount-transfer';
      default: return '';
    }
  }
}