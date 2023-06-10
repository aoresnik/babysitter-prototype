import {TestBed} from '@angular/core/testing';

import {CommandRunSessionService} from './command-run-session.service';

describe('CommandRunSessionService', () => {
  let service: CommandRunSessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CommandRunSessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
