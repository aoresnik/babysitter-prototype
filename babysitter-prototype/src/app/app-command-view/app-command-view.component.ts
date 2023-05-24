import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Observable, switchMap} from "rxjs";

/**
 * LEGACY - remove if it turns out to be unnecessary
 */
@Component({
  selector: 'app-app-command-view',
  templateUrl: './app-command-view.component.html',
  styleUrls: ['./app-command-view.component.css']
})
export class AppCommandViewComponent implements OnInit {
  constructor(private route: ActivatedRoute) {}

  scriptSourceID?: string | null;
  scriptID?: string | null;

  ngOnInit() {
    console.log("AppCommandViewComponent.ngOnInit");
    this.scriptSourceID = this.route.snapshot.paramMap.get('source_id');
    this.scriptID = this.route.snapshot.paramMap.get('script_id');
  }
}
