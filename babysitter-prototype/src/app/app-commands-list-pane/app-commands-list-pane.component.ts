import { Component } from '@angular/core';
import {ScriptError, ScriptResult, ScriptsServiceService} from "../scripts-service.service";

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

  constructor(private scriptsService: ScriptsServiceService) {
  }

  ngOnInit(): void {
    this.scriptsService.getScripts().subscribe(res => {
      console.log(res);
      this.scriptsList = res;
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
        this.scriptError = "";
      } else if (res instanceof ScriptError) {
        const error: ScriptError = res;
        console.log(`Error while executing script: ${error.errorMsg}`);
        this.scriptRun = script;
        this.scriptResult = "<not run>";
        this.scriptError = error.errorMsg;
      }
    });
  }
}
