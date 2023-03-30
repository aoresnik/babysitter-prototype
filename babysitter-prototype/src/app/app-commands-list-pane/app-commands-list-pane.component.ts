import { Component } from '@angular/core';
import {ScriptsServiceService} from "../scripts-service.service";

@Component({
  selector: 'app-app-commands-list-pane',
  templateUrl: './app-commands-list-pane.component.html',
  styleUrls: ['./app-commands-list-pane.component.css']
})
export class AppCommandsListPaneComponent {
  commands: any;

  scriptsList: string[] = [];

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
  }
}
