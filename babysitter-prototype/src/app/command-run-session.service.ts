import {Injectable} from '@angular/core';
import {CommandBabysittingWebsocketConnection, CommandBabysittingWebsocketService} from "./command-babysitting-websocket.service";
import {environment} from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CommandRunSessionService {
  constructor(private wsService: CommandBabysittingWebsocketService) {
  }

  messagesForSession(commandExecutionId: string): CommandBabysittingWebsocketConnection {
    return this.wsService.connect(environment.serverRootURLWS + `/api/v1/commands/session/${commandExecutionId}/websocket`);
  }
}
