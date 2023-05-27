import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-app-command-page',
  templateUrl: './app-command-page.component.html',
  styleUrls: ['./app-command-page.component.css']
})
export class AppCommandPageComponent implements OnInit {
  constructor(private route: ActivatedRoute) {
  }

  scriptSourceID?: string | null;
  scriptID?: string | null;

  ngOnInit() {
    console.log("AppCommandViewComponent.ngOnInit");

    this.route.paramMap
      .subscribe(paramMap => {
          console.log(paramMap);
          this.scriptSourceID = paramMap.get('source_id');
          this.scriptID = paramMap.get('script_id');
        }
      );
  }
}
