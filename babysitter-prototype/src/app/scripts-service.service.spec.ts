import { TestBed } from '@angular/core/testing';

import { ScriptsServiceService } from './scripts-service.service';

describe('ScriptsServiceService', () => {
  let service: ScriptsServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScriptsServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
