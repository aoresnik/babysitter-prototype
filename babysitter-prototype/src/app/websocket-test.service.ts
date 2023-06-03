import {Injectable} from '@angular/core';
import {Observable, Observer, Subject} from "rxjs";

export class ScriptWebsocketConnection {
  ws: WebSocket;

  subject: Subject<any>;

  constructor(public url: string) {
    this.ws = new WebSocket(url);
    console.log("Successfully connected: " + url);

    let observable = Observable.create((obs: Observer<MessageEvent>) => {
      this.ws.onmessage = obs.next.bind(obs);
      this.ws.onerror = obs.error.bind(obs);
      this.ws.onclose = obs.complete.bind(obs);
      return this.ws.close.bind(this.ws);
    });
    let observer = {
      next: (data: Object) => {
        console.log("next");
        if (this.ws.readyState === WebSocket.OPEN) {
          this.ws.send(JSON.stringify(data));
        }
      },
    };
    this.subject = Subject.create(observer, observable);
  }
}

/**
 * Based on: https://tutorialedge.net/typescript/angular/angular-websockets-tutorial/
 */
@Injectable({
  providedIn: 'root'
})
export class WebsocketTestService {

  public connect(url: string): ScriptWebsocketConnection {
    return new ScriptWebsocketConnection(url);
  }
}
