import {Component, ViewChild} from '@angular/core';
import {ScriptError, ScriptResult, ScriptsServiceService} from "../scripts-service.service";
import {NgTerminal} from "ng-terminal";
import {ScriptRunSessionService} from "../script-run-session.service";

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

@Component({
  selector: 'app-app-commands-list-pane',
  templateUrl: './app-commands-list-pane.component.html',
  styleUrls: ['./app-commands-list-pane.component.css']
})
export class AppCommandsListPaneComponent {
  commands: any;

  scriptsList: string[] = [];

  scriptRun: string = "";

  scriptResult: string = "";

  scriptError: string = "";

  runsList: ScriptRun[] = [];

  @ViewChild('term', {static: false}) terminal!: NgTerminal;

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
      // TODO: sending of input
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
    });
  }

  runScript(script: string) {
    console.log(`Running script ${ script }`);
    this.scriptsService.runScriptAsync(script).subscribe(res => {
      let runSessionId = res;
      console.log(`Script run session ID: ${runSessionId}`);
      let scriptRun = new ScriptRun(script, runSessionId);
      this.runsList.push(scriptRun);
      this.showRun(scriptRun);
      // TODO: errors, like in the sync case

    });
    // this.scriptsService.runScript(script).subscribe(res => {
    //   if (res instanceof ScriptResult) {
    //     const resultText = res.lines.join("\n");
    //     console.log(`Result of the script: ${res}`);
    //     this.scriptRun = script;
    //     this.scriptResult = resultText;
    //     this.terminal.underlying.reset();
    //     this.terminal.underlying.options.convertEol = true;
    //     this.terminal.write(resultText);
    //     this.scriptError = "";
    //     this.runsList.push(new ScriptRun(script, "TODO: get session ID"));
    //   } else if (res instanceof ScriptError) {
    //     const error: ScriptError = res;
    //     console.log(`Error while executing script: ${error.errorMsg}`);
    //     this.scriptRun = script;
    //     this.terminal.underlying.reset();
    //     this.scriptResult = "<not run>";
    //     this.scriptError = error.errorMsg;
    //     this.runsList.push(new ScriptRun(script, "TODO: get session ID"));
    //   }
    // });
  }

  showRun(run: ScriptRun) {
    console.log("TODO: Show console of run " + run.scriptRunSessionId + " of script " + run.scriptName);
    this.scriptRunSessionService.messagesForSession(run.scriptName, run.scriptRunSessionId).subscribe(msg => {
      console.log("Response from websocket: " + msg);
      this.terminal.underlying.reset();
      this.terminal.underlying.options.convertEol = true;
      this.terminal.write(atob(msg.initialConsoleData));
    });
  }
}
