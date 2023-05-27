import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AppAllElementsTestPageComponent} from "./pages/app-all-elements-test-page/app-all-elements-test-page.component";
import {AppCommandsPageComponent} from "./pages/app-commands-page/app-commands-page.component";
import {AppCommandPageComponent} from "./pages/app-command-page/app-command-page.component";
import {AppCommandExecutionsPage} from "./pages/app-command-executions-page/app-command-executions-page.component";
import {AppCommandNotSelectedSubpageComponent} from "./pages/app-command-not-selected-subpage/app-command-not-selected-subpage.component";
import {
  AppCommandExecutionNotSelectedSubpageComponent
} from "./pages/app-command-execution-not-selected-subpage/app-command-execution-not-selected-subpage.component";
import {
  AppCommandExecutionPageComponent
} from "./pages/app-command-execution-page/app-command-execution-page.component";

const routes: Routes = [
  // PRODUCTION pages
  // Show commands with searchable list of commands
  {
    path: 'commands', component: AppCommandsPageComponent,
    children: [
      {path: '', redirectTo: 'list', pathMatch: 'full'},
      {path: 'list', component: AppCommandNotSelectedSubpageComponent},
      {path: 'command/:source_id/:script_id', component: AppCommandPageComponent},
    ]
  },

  // Show command execution with searchable list of executions
  {
    path: 'executions', component: AppCommandExecutionsPage,
    children: [
      {path: '', redirectTo: 'list', pathMatch: 'full'},
      {path: 'list', component: AppCommandExecutionNotSelectedSubpageComponent},
      {path: 'execution/:source_id/:script_id', component: AppCommandExecutionPageComponent},
    ]
  },

  // Show command without list of commands
  {path: 'command/:source_id/:script_id', component: AppCommandPageComponent},
  {path: 'execution/:source_id/:script_id', component: AppCommandPageComponent},


  // TEST Pages
  {path: '', component: AppAllElementsTestPageComponent}, // TODO: map this to the "home" page, with recent commands etc.
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
