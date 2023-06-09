import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {TableModule} from 'primeng/table';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {AppAllElementsTestPageComponent} from './pages/app-all-elements-test-page/app-all-elements-test-page.component';
import {HttpClientModule} from '@angular/common/http';
import {NgTerminalModule} from 'ng-terminal';
import {AppCommandsPageComponent} from './pages/app-commands-page/app-commands-page.component';
import {AppCommandExecutionsPage} from './pages/app-command-executions-page/app-command-executions-page.component';
import {AppCommandPageComponent} from './pages/app-command-page/app-command-page.component';
import {
  AppCommandExecutionPageComponent
} from './pages/app-command-execution-page/app-command-execution-page.component';
import {
  AppCommandNotSelectedSubpageComponent
} from './pages/app-command-not-selected-subpage/app-command-not-selected-subpage.component';
import {AppCommandsListPaneComponent} from './components/app-commands-list-pane/app-commands-list-pane.component';
import {TreeModule} from 'primeng/tree';
import {
  AppCommandExecutionNotSelectedSubpageComponent
} from './pages/app-command-execution-not-selected-subpage/app-command-execution-not-selected-subpage.component';
import {AppExecutionsListPaneComponent} from './components/app-executions-list-pane/app-executions-list-pane.component';
import {AppHomePageComponent} from './pages/app-home-page/app-home-page.component';
import { ApiModule as BabysitterServerApiModule } from './babysitter-server-api/api/v1/api.module';
import { Configuration as BabysitterServerConfiguration, ConfigurationParameters as BabysitterServerConfigurationParameters} from './babysitter-server-api/api/v1';

export function babysitterServerConfiguration(): BabysitterServerConfiguration {
  const params: BabysitterServerConfigurationParameters = {
    basePath: 'http://localhost:8080',
  };
  return new BabysitterServerConfiguration(params);
}

@NgModule({
  declarations: [
    AppComponent,
    AppAllElementsTestPageComponent,
    AppCommandsPageComponent,
    AppCommandExecutionsPage,
    AppCommandPageComponent,
    AppCommandExecutionPageComponent,
    AppCommandNotSelectedSubpageComponent,
    AppCommandsListPaneComponent,
    AppCommandExecutionNotSelectedSubpageComponent,
    AppExecutionsListPaneComponent,
    AppHomePageComponent,
  ],
  imports: [
    NgTerminalModule,
    BrowserModule,
    AppRoutingModule,
    TableModule,
    HttpClientModule,
    TreeModule,
    BabysitterServerApiModule.forRoot(babysitterServerConfiguration),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
