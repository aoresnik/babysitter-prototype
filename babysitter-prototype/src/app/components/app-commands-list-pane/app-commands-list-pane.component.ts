import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {CommandData, CommandsResourceService} from "../../babysitter-server-api/api/v1";
import {catchError, Observable, of} from "rxjs";

@Component({
  selector: 'app-app-commands-list-pane',
  templateUrl: './app-commands-list-pane.component.html',
  styleUrls: ['./app-commands-list-pane.component.css']
})
export class AppCommandsListPaneComponent implements OnInit {
  // commandsAsTree: TreeNode[] = [ {
  //   key: '0',
  //   label: 'Local commands',
  //   data: 'Documents Folder',
  //   icon: 'pi pi-fw pi-inbox',
  //   children: [
  //     { key: '0-0-0', label: 'test.sh', icon: 'pi pi-fw pi-file', data: 'Expenses Document' },
  //     { key: '0-0-1', label: 'test2.sh', icon: 'pi pi-fw pi-file', data: 'Resume Document' }
  //   ]
  // } ];
  commandsList?: CommandData[] | undefined;

  constructor(private commandsResourceService: CommandsResourceService, private router: Router) {

  }

  ngOnInit(): void {
    this.commandsResourceService.apiV1CommandsGet()
      .pipe(
        catchError(this.handleError<CommandData[]>('getHeroes', undefined))
      )
      .subscribe(res => {
        console.log(res);
        this.commandsList = res;
      });
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      console.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  selectCommand(command: CommandData) {
    this.router.navigateByUrl(`/commands/command/${command.commandSourceId}/${command.commandId}`)
      .then(r => console.log(`Navigation successful: ${r}`));
  }
}
