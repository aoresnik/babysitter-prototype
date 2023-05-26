import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import {TableModule} from 'primeng/table';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AppAllElementsTestPageComponent } from './pages/app-all-elements-test-page/app-all-elements-test-page.component';
import { HttpClientModule } from '@angular/common/http';
import { NgTerminalModule } from 'ng-terminal';
import { AppCommandViewComponent } from './components/app-command-view/app-command-view.component';
import { AppCommandExecutionViewComponent } from './components/app-command-execution-view/app-command-execution-view.component';
import { AppAllCommandsPageComponent } from './pages/app-all-comands-page/app-all-commands-page.component';
import { AppActiveRunsPageComponent } from './pages/app-active-runs-page/app-active-runs-page.component';
import { AppHistoryPageComponent } from './pages/app-history-page/app-history-page.component';
import { AppCommandPageComponent } from './pages/app-command-page/app-command-page.component';
import { AppCommandExecutionPageComponent } from './pages/app-command-execution-page/app-command-execution-page.component';
import { AppCommandsListPageComponent } from './pages/app-commands-list-page/app-commands-list-page.component';
import { AppCommandsListPaneComponent } from './components/app-commands-list-pane/app-commands-list-pane.component';

@NgModule({
  declarations: [
    AppComponent,
    AppAllElementsTestPageComponent,
    AppCommandViewComponent,
    AppCommandExecutionViewComponent,
    AppAllCommandsPageComponent,
    AppActiveRunsPageComponent,
    AppHistoryPageComponent,
    AppCommandPageComponent,
    AppCommandExecutionPageComponent,
    AppCommandsListPageComponent,
    AppCommandsListPaneComponent,
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
