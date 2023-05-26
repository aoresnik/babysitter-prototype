import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AppCommandViewComponent} from "./components/app-command-view/app-command-view.component";
import {AppCommandExecutionViewComponent} from "./components/app-command-execution-view/app-command-execution-view.component";
import {AppCommandsListPaneComponent} from "./components/app-commands-list-pane/app-commands-list-pane.component";
import {AppAllCoomandsPageComponent} from "./pages/app-all-coomands-page/app-all-coomands-page.component";
import {AppCommandPageComponent} from "./pages/app-command-page/app-command-page.component";
import {AppActiveRunsPageComponent} from "./pages/app-active-runs-page/app-active-runs-page.component";
import {AppHistoryPageComponent} from "./pages/app-history-page/app-history-page.component";
import {AppCommandsListPageComponent} from "./pages/app-commands-list-page/app-commands-list-page.component";

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
