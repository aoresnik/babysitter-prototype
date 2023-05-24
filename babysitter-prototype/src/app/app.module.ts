import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import {TableModule} from 'primeng/table';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AppCommandsListPaneComponent } from './app-commands-list-pane/app-commands-list-pane.component';
import { HttpClientModule } from '@angular/common/http';
import { NgTerminalModule } from 'ng-terminal';
import { AppCommandViewComponent } from './app-command-view/app-command-view.component';
import { AppCommandExecutionViewComponent } from './app-command-execution-view/app-command-execution-view.component';
import { AppAllCoomandsPageComponent } from './app-all-coomands-page/app-all-coomands-page.component';
import { AppActiveRunsPageComponent } from './app-active-runs-page/app-active-runs-page.component';
import { AppHistoryPageComponent } from './app-history-page/app-history-page.component';
import { AppCommandPageComponent } from './app-command-page/app-command-page.component';
import { AppCommandExecutionPageComponent } from './app-command-execution-page/app-command-execution-page.component';

@NgModule({
  declarations: [
    AppComponent,
    AppCommandsListPaneComponent,
    AppCommandViewComponent,
    AppCommandExecutionViewComponent,
    AppAllCoomandsPageComponent,
    AppActiveRunsPageComponent,
    AppHistoryPageComponent,
    AppCommandPageComponent,
    AppCommandExecutionPageComponent,
  ],
  imports: [
    NgTerminalModule,
    BrowserModule,
    AppRoutingModule,
    TableModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
