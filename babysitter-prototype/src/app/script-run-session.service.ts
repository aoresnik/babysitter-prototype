import { Injectable} from '@angular/core';
import {Observable, Subject} from "rxjs";
import { map } from 'rxjs/operators';
import {WebsocketTestService} from "./websocket-test.service";

@Injectable({
  providedIn: 'root'
})
export class ScriptRunSessionService {
  public messages: Subject<string>;

  constructor(wsService: WebsocketTestService) {
    this.messages = <Subject<string>>wsService.connect(CHAT_URL).pipe(map(
      (response: MessageEvent): string => {
        //let data = JSON.parse(response.data);
        return response.data;
      }
    ));
  }
}
