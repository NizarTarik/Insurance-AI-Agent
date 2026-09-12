import { TestBed } from '@angular/core/testing';

import { ProspectServiceService } from './prospect.service';

describe('ProspectServiceService', () => {
  let service: ProspectServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProspectServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
