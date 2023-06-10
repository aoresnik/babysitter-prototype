import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {NgTerminal} from "ng-terminal";
import {CommandBabysittingWebsocketConnection} from "../../command-babysitting-websocket.service";
import {CommandRunSessionService} from "../../command-run-session.service";
import {environment} from "../../../environments/environment";
import {CommandExecutionResourceService} from "../../babysitter-server-api/api/v1";

@Component({
  selector: 'app-app-command-execution-page',
  templateUrl: './app-command-execution-page.component.html',
  styleUrls: ['./app-command-execution-page.component.css']
})
export class AppCommandExecutionPageComponent implements OnInit, AfterViewInit {
  executionID?: string | null;

  commandRun: string = "";

  errorText: string = "";

  commandCompleted?: boolean;

  exitCode?: number;

  @ViewChild('term', {static: false}) terminal!: NgTerminal;

  private messages?: CommandBabysittingWebsocketConnection;

  commandId?: string;

  constructor(private route: ActivatedRoute,
              private commandRunSessionService: CommandRunSessionService,
              private commandExecutionResourceService: CommandExecutionResourceService,
              private router: Router) {
  }

  ngOnInit() {
    this.route.paramMap
      .subscribe(paramMap => {
          console.log(paramMap);
          this.executionID = paramMap.get('execution_id');
          if (this.executionID != null) {
            this.showExecution(this.executionID);
          }
        }
      );
  }

  ngAfterViewInit(){
    this.terminal.onData().subscribe((input) => {
      if (this.commandRun && !this.commandCompleted && this.messages) {
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
        this.messages.subject.next({inputData: btoa(input)});
      } else {
        console.log("No active run, ignoring input");
      }
    });
  }

  showExecution(executionId: string) {
    console.log("Show console of execution " + executionId);

    if (this.messages) {
      console.log("Unsubscribing from previous session");
      this.messages.ws.close();
    }

    this.messages = this.commandRunSessionService.messagesForSession(executionId);
    this.messages.subject.subscribe(response => {
      let msg = JSON.parse(response.data);
      console.log("Response from websocket: " + msg);
      if (msg.initialConsoleData !== undefined) {
        // Documentation of terminal class of this.terminal.underlying http://xtermjs.org/docs/api/terminal/classes/terminal/
        this.terminal.setRows(43);
        this.terminal.setCols(80);
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
        this.errorText = msg.errorText;
      } else {
        this.errorText = "";
      }
    });

    this.commandExecutionResourceService.apiV1ExecutionsExecutionIdGet(executionId).subscribe(response => {
      this.commandId = response.commandId;
    });
  }

  protected readonly environment = environment;
}
