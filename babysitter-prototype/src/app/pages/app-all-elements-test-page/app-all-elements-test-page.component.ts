import {Component, ViewChild} from '@angular/core';
import {NgTerminal} from "ng-terminal";
import {CommandRunSessionService} from "../../command-run-session.service";
import {CommandBabysittingWebsocketConnection} from "../../command-babysitting-websocket.service";
import {CommandData, CommandInputData, CommandsResourceService} from "../../babysitter-server-api/api/v1";

export class CommandExecution {
  constructor(commandScript: string, commandExecutionId: string, date: Date = new Date() ) {
    this.commandName = commandScript;
    this.commandExecutionId = commandExecutionId;
    this.date = date;
  }
  date: Date;
  commandName: string;
  commandExecutionId: string;
}

/**
 * NOTE: DE FACTO serves as a kind of test page for widgets and components
 */
@Component({
  selector: 'app-all-elements-test-page',
  templateUrl: './app-all-elements-test-page.component.html',
  styleUrls: ['./app-all-elements-test-page.component.css']
})
export class AppAllElementsTestPageComponent {
  commands: any;

  commandsList: CommandData[] = [];

  commandRun: string = "";

  // scriptResult: string = "";

  commandError: string = "";

  commandCompleted?: boolean;

  exitCode?: number;

  runsList: CommandExecution[] = [];

  activeRun?: CommandExecution;

  @ViewChild('term', {static: false}) terminal!: NgTerminal;

  private messages?: CommandBabysittingWebsocketConnection;

  constructor(private commandsResourceService: CommandsResourceService, private commandRunSessionService: CommandRunSessionService) {

  }

  ngOnInit(): void {
    this.commandsResourceService.apiV1CommandsGet().subscribe(res => {
      console.log(res);
      this.commandsList = res;
    });
  }

  ngAfterViewInit(){
    this.terminal.onData().subscribe((input) => {
      if (this.activeRun && this.messages) {
        // if (input === '\r') { // Carriage Return (When Enter is pressed)
        //   this.terminal.write('prompt>');
        // } else if (input === '\u007f') { // Delete (When Backspace is pressed)
        //   if (this.terminal.underlying.buffer.active.cursorX > 2) {
        //     this.terminal.write('\b \b');
        //   }
        // } else if (input === '\u0003') { // End of Text (When Ctrl and C are pressed)
        //   this.terminal.write('^C');
        //   this.terminal.write('prompt>');
        // }else
        //   this.terminal.write(input);
        let commandInputData: CommandInputData = {inputData: btoa(input)};
        this.messages.subject.next(commandInputData);
      } else {
        console.log("No active run, ignoring input");
      }
    });
  }

  runCommand(command: CommandData) {
    console.log(`Running command ${ command.commandId }, name ${ command.commandName }`);
    // FIXME: change API to string ID and convert on server side
    this.commandsResourceService.apiV1CommandsSourcesCommandSourceIdCommandIdRunAsyncPost(parseInt(command.commandId, 10), command.commandSourceId).subscribe(res => {
      let runSessionId = res;
      console.log(`Script run session ID: ${runSessionId}`);
      let commandExecution = new CommandExecution(command.commandName, runSessionId);
      this.runsList.push(commandExecution);
      this.showRun(commandExecution);
    });
  }

  showRun(run: CommandExecution) {
    console.log("Show console of run " + run.commandExecutionId + " of name " + run.commandName);
    this.activeRun = run;
    if (this.messages) {
      console.log("Unsubscribing from previous session");
      this.messages.ws.close();
    }

    this.messages = this.commandRunSessionService.messagesForSession(run.commandExecutionId);
    this.messages.subject.subscribe(response => {
      let msg = JSON.parse(response.data);
      console.log("Response from websocket: " + msg);
      if (msg.initialConsoleData !== undefined) {
        // Documentation of terminal class of this.terminal.underlying http://xtermjs.org/docs/api/terminal/classes/terminal/
        this.terminal.underlying.reset();
        this.terminal.underlying.options.convertEol = true;
        this.terminal.write(atob(msg.initialConsoleData));
      } else if (msg.incrementalConsoleData !== undefined && msg.incrementalConsoleData) {
        this.terminal.write(atob(msg.incrementalConsoleData));
      }
      if (msg.commandRun !== undefined) {
        this.commandRun = msg.commandRun;
      }
      if (msg.commandCompleted !== undefined) {
        this.commandCompleted = msg.commandCompleted;
      }
      if (msg.exitCode !== undefined) {
        this.exitCode = msg.exitCode;
      }
      if (msg.errorText) {
        this.commandError = msg.errorText;
      } else {
        this.commandError = "";
      }
    });
  }
}
