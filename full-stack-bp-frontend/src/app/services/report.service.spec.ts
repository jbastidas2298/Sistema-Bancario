/* tslint:disable:no-unused-variable */

import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReportService } from './report.service';

describe('Service: Report', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReportService]
    });
  });

  it('should be created', () => {
    const service = TestBed.inject(ReportService);
    expect(service).toBeTruthy();
  });
});