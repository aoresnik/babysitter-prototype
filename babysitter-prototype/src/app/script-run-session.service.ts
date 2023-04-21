import { Injectable} from '@angular/core';
import {Observable, Subject} from "rxjs";
import { map } from 'rxjs/operators';
import {WebsocketTestService} from "./websocket-test.service";

const CHAT_URL = "ws://localhost:8080/api/v1/scripts/1/session/1/websocket";

@Injectable({
  providedIn: 'root'
})
export class ScriptRunSessionService {
  constructor(private wsService: WebsocketTestService) {
  }

  messagesForSession(scriptName: string, scriptRunSessionId: string): Subject<string> {
    let result = <Subject<string>>this.wsService.connect(`ws://localhost:8080/api/v1/scripts/${scriptName}/session/${scriptRunSessionId}/websocket`).pipe(map(
      (response: MessageEvent): string => {
        //let data = JSON.parse(response.data);
        return response.data;
      }
    ));
    return result;
  }
}
