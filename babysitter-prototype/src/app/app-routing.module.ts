import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AppCommandViewComponent} from "./app-command-view/app-command-view.component";
import {AppCommandExecutionViewComponent} from "./app-command-execution-view/app-command-execution-view.component";
import {AppCommandsListPaneComponent} from "./app-commands-list-pane/app-commands-list-pane.component";
import {AppAllCoomandsPageComponent} from "./app-all-coomands-page/app-all-coomands-page.component";
import {AppCommandPageComponent} from "./app-command-page/app-command-page.component";
import {AppActiveRunsPageComponent} from "./app-active-runs-page/app-active-runs-page.component";
import {AppHistoryPageComponent} from "./app-history-page/app-history-page.component";
import {AppCommandsListPageComponent} from "./app-commands-list-page/app-commands-list-page.component";

const routes: Routes = [
  // PRODUCTION pages
  {
    path: 'commands', component: AppAllCoomandsPageComponent,
    children: [
      {path: '', redirectTo: 'list', pathMatch: 'full'},
      {path: 'list', component: AppCommandsListPageComponent},
      {path: 'command/:source_id/:script_id', component: AppCommandPageComponent},
    ]
  },

  {path: 'command-executions/active', component: AppActiveRunsPageComponent},
  {path: 'command-executions/history', component: AppHistoryPageComponent},

  // TEST Pages
  {path: 'command/:source_id/:script_id', component: AppCommandViewComponent}, // TODO: this should be a page with cmd list on the left and cmd view on the right
  {path: 'command-exec/:source_id/:script_id/:execution_id', component: AppCommandExecutionViewComponent},

  {path: '', component: AppCommandsListPaneComponent}, // TODO: map this to the "home" page, with recent commands etc.
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
