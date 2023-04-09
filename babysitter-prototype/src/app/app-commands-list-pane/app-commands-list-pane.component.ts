import {Component, ViewChild} from '@angular/core';
import {ScriptError, ScriptResult, ScriptsServiceService} from "../scripts-service.service";
import {NgTerminal} from "ng-terminal";

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

  @ViewChild('term', {static: false}) terminal!: NgTerminal;

  constructor(private scriptsService: ScriptsServiceService) {

  }

  ngOnInit(): void {
    this.scriptsService.getScripts().subscribe(res => {
      console.log(res);
      this.scriptsList = res;
    });
  }

  ngAfterViewInit(){
    this.terminal.onData().subscribe((input) => {
      if (input === '\r') { // Carriage Return (When Enter is pressed)
        this.terminal.write('prompt>');
      } else if (input === '\u007f') { // Delete (When Backspace is pressed)
        if (this.terminal.underlying.buffer.active.cursorX > 2) {
          this.terminal.write('\b \b');
        }
      } else if (input === '\u0003') { // End of Text (When Ctrl and C are pressed)
        this.terminal.write('^C');
        this.terminal.write('prompt>');
      }else
        this.terminal.write(input);
    });
  }

  runScript(script: string) {
    console.log(`Running script ${ script }`);
    this.scriptsService.runScript(script).subscribe(res => {
      if (res instanceof ScriptResult) {
        const result: ScriptResult = res;
        const resultText = result.lines.join("\n");
        console.log(`Result of the script: ${res}`);
        this.scriptRun = script;
        this.scriptResult = resultText;
        this.terminal.underlying.reset();
        this.terminal.underlying.options.convertEol = true;
        this.terminal.write(resultText);
        this.scriptError = "";
      } else if (res instanceof ScriptError) {
        const error: ScriptError = res;
        console.log(`Error while executing script: ${error.errorMsg}`);
        this.scriptRun = script;
        this.terminal.underlying.reset();
        this.scriptResult = "<not run>";
        this.scriptError = error.errorMsg;
      }
    });
  }
}
