import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ScriptRun} from "../app-all-elements-test-page/app-all-elements-test-page.component";
import {ScriptsServiceService} from "../../scripts-service.service";

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
  constructor(private route: ActivatedRoute, private scriptsService: ScriptsServiceService, private router: Router) {
  }

  commandSourceId?: number | null;
  scriptID?: string | null;

  ngOnInit() {
    this.route.paramMap
      .subscribe(paramMap => {
          console.log(paramMap);
        let sourceIdStr = paramMap.get('source_id')
        if (sourceIdStr != null) {
          this.commandSourceId = parseInt(sourceIdStr, 10);
        } else {
          this.commandSourceId = null;
        }
        this.scriptID = paramMap.get('script_id');
        }
      );
  }

  runScript(script: any, openConsole: boolean) {
    console.log(`Running script ${ script }`);
    if (this.commandSourceId != null && this.scriptID != null) {
      this.scriptsService.runScriptAsync(this.commandSourceId, this.scriptID).subscribe(res => {
        let runSessionId = res;
        console.log(`Script run session ID: ${runSessionId}`);
        if (openConsole) {
          console.log(`Opening command execution console for: ${runSessionId}`);
          let scriptRun = new ScriptRun(script.commandId, runSessionId);
          this.showRun(scriptRun);
        }
      });
    }
  }

  showRun(run: ScriptRun) {
    console.log("Show console of run " + run.scriptRunSessionId + " of script " + run.scriptName);
    this.router.navigateByUrl(`/execution/${run.scriptRunSessionId}`)
      .then(r => console.log(`Navigation successful: ${r}`));
  }
}

