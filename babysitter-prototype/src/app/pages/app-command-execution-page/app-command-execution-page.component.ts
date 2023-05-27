import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ScriptsServiceService} from "../../scripts-service.service";

@Component({
  selector: 'app-app-command-execution-page',
  templateUrl: './app-command-execution-page.component.html',
  styleUrls: ['./app-command-execution-page.component.css']
})
export class AppCommandExecutionPageComponent implements OnInit {
  executionID?: string | null;

  constructor(private route: ActivatedRoute, private scriptsService: ScriptsServiceService, private router: Router) {
  }

  ngOnInit() {
    this.route.paramMap
      .subscribe(paramMap => {
          console.log(paramMap);
          this.executionID = paramMap.get('execution_id');
        }
      );
  }

}
