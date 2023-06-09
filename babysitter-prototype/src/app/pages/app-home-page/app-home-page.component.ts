import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {CommandLastUsedData, CommandMostUsedData, CommandsResourceService} from "../../babysitter-server-api/api/v1";

@Component({
  selector: 'app-app-home-page',
  templateUrl: './app-home-page.component.html',
  styleUrls: ['./app-home-page.component.css']
})
export class AppHomePageComponent {
  mostUsedCommandsList?: CommandMostUsedData[];
  lastCommandsList?: CommandLastUsedData[];

  constructor(private commandsResourceService: CommandsResourceService, private router: Router) {
  }

  ngOnInit(): void {
    this.commandsResourceService.apiV1CommandsLastUsedGet().subscribe(res => {
      console.log(res);
      this.lastCommandsList = res;
    });
    this.commandsResourceService.apiV1CommandsMostUsedGet().subscribe(res => {
      console.log(res);
      this.mostUsedCommandsList = res;
    });
  }

  selectCommand(script: any) {
    this.router.navigateByUrl(`/commands/command/${script.commandSourceId}/${script.commandId}`)
      .then(r => console.log(`Navigation successful: ${r}`));
  }
}
