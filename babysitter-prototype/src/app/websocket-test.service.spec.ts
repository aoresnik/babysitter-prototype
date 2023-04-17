import { TestBed } from '@angular/core/testing';

import { WebsocketTestService } from './websocket-test.service';

describe('WebsocketTestService', () => {
  let service: WebsocketTestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebsocketTestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
