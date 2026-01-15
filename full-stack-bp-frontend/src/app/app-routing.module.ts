import { NgModule } from '@angular/core';
import { LoginComponent } from './view/login/login.component';
import { DashboardComponent } from './view/dashboard/dashboard.component';
import { RouterModule, Routes } from '@angular/router';
import { ClientsComponent } from './view/clients/clients.component';
import { AccountsComponent } from './view/accounts/accounts.component';
import { TransactionsComponent } from './view/transactions/transactions.component';
import { ReportsComponent } from './view/reports/reports.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: DashboardComponent,
    children: [
      { path: 'clients', component: ClientsComponent },
      { path: 'accounts', component: AccountsComponent },
      { path: 'transactions', component: TransactionsComponent },
      { path: 'reports', component: ReportsComponent },
      { path: '', redirectTo: 'clients', pathMatch: 'full' }
    ]
  }
];
@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
