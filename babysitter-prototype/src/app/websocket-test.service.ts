import { Injectable } from '@angular/core';
import {Observable, Observer, Subject} from "rxjs";


@Injectable({
  providedIn: 'root'
})
export class WebsocketTestService {

  public connect(url: string): Subject<MessageEvent> {
      let subject = this.create(url);
      console.log("Successfully connected: " + url);
      return subject;
  }

  // Based on: https://tutorialedge.net/typescript/angular/angular-websockets-tutorial/
  private create(url: string): Subject<MessageEvent> {
    let ws = new WebSocket(url);

    let observable = Observable.create((obs: Observer<MessageEvent>) => {
      ws.onmessage = obs.next.bind(obs);
      ws.onerror = obs.error.bind(obs);
      ws.onclose = obs.complete.bind(obs);
      return ws.close.bind(ws);
    });
    let observer = {
      next: (data: Object) => {
        console.log("next");
        if (ws.readyState === WebSocket.OPEN) {
          ws.send(JSON.stringify(data));
        }
      },
    };
    return Subject.create(observer, observable);
  }

}
