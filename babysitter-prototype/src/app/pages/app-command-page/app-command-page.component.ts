import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

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
