import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-app-command-execution-view',
  templateUrl: './app-command-execution-view.component.html',
  styleUrls: ['./app-command-execution-view.component.css']
})
export class AppCommandExecutionViewComponent implements OnInit {
  scriptSourceID?: string | null;
  scriptID?: string | null;
  executionID?: string | null;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    console.log("AppCommandViewComponent.ngOnInit");
    this.scriptSourceID = this.route.snapshot.paramMap.get('source_id');
    this.scriptID = this.route.snapshot.paramMap.get('script_id');
    this.executionID = this.route.snapshot.paramMap.get('execution_id');
  }
}
