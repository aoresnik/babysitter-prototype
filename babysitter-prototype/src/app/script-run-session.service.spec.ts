import { TestBed } from '@angular/core/testing';

import { ScriptRunSessionService } from './script-run-session.service';

describe('ScriptRunSessionService', () => {
  let service: ScriptRunSessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScriptRunSessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
