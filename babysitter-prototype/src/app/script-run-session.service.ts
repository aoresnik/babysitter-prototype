import { Injectable} from '@angular/core';
import {Observable, Subject} from "rxjs";
import { map } from 'rxjs/operators';
import {ScriptWebsocketConnection, WebsocketTestService} from "./websocket-test.service";

@Injectable({
  providedIn: 'root'
})
export class ScriptRunSessionService {
  constructor(private wsService: WebsocketTestService) {
  }

  messagesForSession(scriptName: string, scriptRunSessionId: string): ScriptWebsocketConnection {
    return this.wsService.connect(`ws://localhost:8080/api/v1/scripts/session/${scriptRunSessionId}/websocket`);
  }
}
