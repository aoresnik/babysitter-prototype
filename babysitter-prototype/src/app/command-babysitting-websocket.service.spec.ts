import {TestBed} from '@angular/core/testing';

import {CommandBabysittingWebsocketService} from './command-babysitting-websocket.service';

describe('CommandBabysittingWebsocketService', () => {
  let service: CommandBabysittingWebsocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CommandBabysittingWebsocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
