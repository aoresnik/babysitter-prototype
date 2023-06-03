import {Injectable} from '@angular/core';
import {ScriptWebsocketConnection, WebsocketTestService} from "./websocket-test.service";
import {environment} from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ScriptRunSessionService {
  constructor(private wsService: WebsocketTestService) {
  }

  messagesForSession(scriptRunSessionId: string): ScriptWebsocketConnection {
    return this.wsService.connect(environment.serverRootURLWS + `/api/v1/commands/session/${scriptRunSessionId}/websocket`);
  }
}
