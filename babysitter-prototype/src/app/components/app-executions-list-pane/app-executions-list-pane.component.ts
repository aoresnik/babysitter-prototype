import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {ScriptExecutionsService} from "../../script-executions.service";

@Component({
  selector: 'app-executions-list-pane',
  templateUrl: './app-executions-list-pane.component.html',
  styleUrls: ['./app-executions-list-pane.component.css']
})
export class AppExecutionsListPaneComponent {
  scriptExecutionsList?: any[];

  constructor(private scriptExecutionsService: ScriptExecutionsService, private router: Router) {

  }

  ngOnInit(): void {
    this.scriptExecutionsService.getCommandExecutions().subscribe(res => {
      console.log(res);
      this.scriptExecutionsList = res;
    });
  }

  selectExecution(execution: any) {
    this.router.navigateByUrl(`/executions/execution/${execution.commandExecutionId}`)
      .then(r => console.log(`Navigation successful: ${r}`));
  }
}
