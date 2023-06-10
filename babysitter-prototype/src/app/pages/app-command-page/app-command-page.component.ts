import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CommandExecution} from "../app-all-elements-test-page/app-all-elements-test-page.component";
import {CommandsResourceService} from "../../babysitter-server-api/api/v1";

/**
 * NOTE: The page must be able to be embedded in the router-outlet of the app-commands-page.component.html and also on
 * the top level router outlet (for performance reasons, some actions may link directly to command)
 */
@Component({
  selector: 'app-app-command-page',
  templateUrl: './app-command-page.component.html',
  styleUrls: ['./app-command-page.component.css']
})
export class AppCommandPageComponent implements OnInit {
  constructor(private route: ActivatedRoute, private commandsResourceService: CommandsResourceService, private router: Router) {
  }

  commandSourceId?: number | null;
  commandId?: number | null;

  ngOnInit() {
    this.route.paramMap
      .subscribe(paramMap => {
        console.log(paramMap);
        let sourceIdStr = paramMap.get('command_source_id')
        if (sourceIdStr != null) {
          this.commandSourceId = parseInt(sourceIdStr, 10);
        } else {
          this.commandSourceId = null;
        }
        let commandIdStr = paramMap.get('command_id');
        if (commandIdStr != null) {
          this.commandId = parseInt(commandIdStr, 10);
        } else {
          this.commandId = null;
        }
      });
  }

  runCommand(command: any, openConsole: boolean) {
    console.log(`Running command ${ command }`);
    if (this.commandSourceId != null && this.commandId != null) {
      this.commandsResourceService.apiV1CommandsSourcesCommandSourceIdCommandIdRunAsyncPost(this.commandId, this.commandSourceId).subscribe(res => {
        let commandExecutionId = res;
        console.log(`Command run session ID: ${commandExecutionId}`);
        if (openConsole) {
          console.log(`Opening command execution console for: ${commandExecutionId}`);
          let commandExecution = new CommandExecution(command.commandId, commandExecutionId);
          this.showRun(commandExecution);
        }
      });
    }
  }

  showRun(run: CommandExecution) {
    console.log("Show console of run " + run.commandExecutionId + " of command command name " + run.commandName);
    this.router.navigateByUrl(`/execution/${run.commandExecutionId}`)
      .then(r => console.log(`Navigation successful: ${r}`));
  }
}

