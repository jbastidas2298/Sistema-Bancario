import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TransactionService, TransactionRequest, Transaction } from './transaction.service';
import { environment } from '../../environments/environment';

describe('TransactionService', () => {
  let service: TransactionService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TransactionService]
    });

    service = TestBed.inject(TransactionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); 
  });

  it('should call POST /transactions/withdraw', () => {
    const request: TransactionRequest = {
      accountNumber: '123456',
      transactionType: 'WITHDRAWAL',
      amount: 200,
      description: 'Test withdrawal'
    };

    const mockResponse: Transaction = {
      id: 1,
      date: '2026-01-01',
      transactionType: 'WITHDRAWAL',
      amount: 200,
      balance: 1800,
      description: 'Test withdrawal',
      account: {
        id: 10,
        accountNumber: '123456',
        accountType: 'SAVINGS',
        client: {
          id: 1,
          name: 'Test User',
          identification: '9999999999'
        }
      }
    };

    service.withdraw(request).subscribe(response => {
      expect(response.id).toBe(1);
      expect(response.amount).toBe(200);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/transactions/withdraw`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);

    req.flush(mockResponse);
  });
});
