import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {CommandExecutionData, CommandExecutionResourceService} from "../../babysitter-server-api/api/v1";

@Component({
  selector: 'app-executions-list-pane',
  templateUrl: './app-executions-list-pane.component.html',
  styleUrls: ['./app-executions-list-pane.component.css']
})
export class AppExecutionsListPaneComponent {
  commandExecutionsList?: CommandExecutionData[];

  constructor(private commandExecutionResourceService: CommandExecutionResourceService, private router: Router) {

  }

  ngOnInit(): void {
    this.commandExecutionResourceService.apiV1ExecutionsGet().subscribe(res => {
      console.log(res);
      this.commandExecutionsList = res;
    });
  }

  selectExecution(execution: CommandExecutionData) {
    this.router.navigateByUrl(`/executions/execution/${execution.commandExecutionId}`)
      .then(r => console.log(`Navigation successful: ${r}`));
  }
}
