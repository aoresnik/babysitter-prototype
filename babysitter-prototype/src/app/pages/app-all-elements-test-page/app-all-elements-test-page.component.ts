import {Component, ViewChild} from '@angular/core';
import {ScriptError, ScriptResult, ScriptsServiceService} from "../../scripts-service.service";
import {NgTerminal} from "ng-terminal";
import {ScriptRunSessionService} from "../../script-run-session.service";
import {Subject} from "rxjs";
import {ScriptWebsocketConnection} from "../../websocket-test.service";

export class ScriptRun {
  constructor(scriptName: string, scriptRunSessionId: string, date: Date = new Date() ) {
    this.scriptName = scriptName;
    this.scriptRunSessionId = scriptRunSessionId;
    this.date = date;
  }
  date: Date;
  scriptName: string;
  scriptRunSessionId: string;
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

  scriptsList: any[] = [];

  scriptRun: string = "";

  scriptResult: string = "";

  scriptError: string = "";

  scriptCompleted?: boolean;

  exitCode?: number;

  runsList: ScriptRun[] = [];

  activeRun?: ScriptRun;

  @ViewChild('term', {static: false}) terminal!: NgTerminal;

  private messages?: ScriptWebsocketConnection;

  constructor(private scriptsService: ScriptsServiceService, private scriptRunSessionService: ScriptRunSessionService) {

  }

  ngOnInit(): void {
    this.scriptsService.getScripts().subscribe(res => {
      console.log(res);
      this.scriptsList = res;
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
        this.messages.subject.next({inputData: btoa(input)});
      } else {
        console.log("No active run, ignoring input");
      }
    });
  }

  runScript(script: any) {
    console.log(`Running script ${ script }`);
    this.scriptsService.runScriptAsync(script.scriptSourceId, script.scriptId).subscribe(res => {
      let runSessionId = res;
      console.log(`Script run session ID: ${runSessionId}`);
      let scriptRun = new ScriptRun(script.scriptId, runSessionId);
      this.runsList.push(scriptRun);
      this.showRun(scriptRun);
    });
  }

  showRun(run: ScriptRun) {
    console.log("Show console of run " + run.scriptRunSessionId + " of script " + run.scriptName);
    this.activeRun = run;
    if (this.messages) {
      console.log("Unsubscribing from previous session");
      this.messages.ws.close();
    }

    this.messages = this.scriptRunSessionService.messagesForSession(run.scriptName, run.scriptRunSessionId);
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
      if (msg.scriptRun !== undefined) {
        this.scriptRun = msg.scriptRun;
      }
      if (msg.scriptCompleted !== undefined) {
        this.scriptCompleted = msg.scriptCompleted;
      }
      if (msg.exitCode !== undefined) {
        this.exitCode = msg.exitCode;
      }
      if (msg.errorText) {
        this.scriptError = msg.errorText;
      } else {
        this.scriptError = "";
      }
    });
  }
}
