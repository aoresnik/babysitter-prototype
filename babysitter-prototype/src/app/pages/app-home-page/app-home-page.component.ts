import {Component} from '@angular/core';
import {ScriptsServiceService} from "../../scripts-service.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-app-home-page',
  templateUrl: './app-home-page.component.html',
  styleUrls: ['./app-home-page.component.css']
})
export class AppHomePageComponent {
  scriptsList?: any[];
  lastCommandsList?: any[];

  constructor(private scriptsService: ScriptsServiceService, private router: Router) {
  }

  ngOnInit(): void {
    this.scriptsService.getMostUsedCommands().subscribe(res => {
      console.log(res);
      this.scriptsList = res;
    });
    this.scriptsService.getLastUsedCommands().subscribe(res => {
      console.log(res);
      this.lastCommandsList = res;
    });
  }

  selectCommand(script: any) {
    this.router.navigateByUrl(`/commands/command/${script.commandSourceId}/${script.commandId}`)
      .then(r => console.log(`Navigation successful: ${r}`));
  }
}
