import {TestBed} from '@angular/core/testing';

import {ScriptExecutionsService} from './script-executions.service';

describe('ScriptExecutionsService', () => {
  let service: ScriptExecutionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScriptExecutionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
