import { Injectable} from '@angular/core';
import {Observable, Subject} from "rxjs";
import { map } from 'rxjs/operators';
import {ScriptWebsocketConnection, WebsocketTestService} from "./websocket-test.service";
import {environment} from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ScriptRunSessionService {
  constructor(private wsService: WebsocketTestService) {
  }

  messagesForSession(scriptRunSessionId: string): ScriptWebsocketConnection {
    return this.wsService.connect(environment.serverRootURLWS + `/api/v1/scripts/session/${scriptRunSessionId}/websocket`);
  }
}
