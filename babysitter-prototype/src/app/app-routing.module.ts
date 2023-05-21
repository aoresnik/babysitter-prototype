import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AppCommandViewComponent} from "./app-command-view/app-command-view.component";
import {AppCommandExecutionViewComponent} from "./app-command-execution-view/app-command-execution-view.component";
import {AppCommandsListPaneComponent} from "./app-commands-list-pane/app-commands-list-pane.component";

const routes: Routes = [
  { path: 'command/:source_id/:script_id', component: AppCommandViewComponent },
  { path: 'command-exec/:source_id/:script_id/:execution_id', component: AppCommandExecutionViewComponent },
  { path: '', component: AppCommandsListPaneComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
