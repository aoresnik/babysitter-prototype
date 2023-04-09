import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import {TableModule} from 'primeng/table';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AppCommandsListPaneComponent } from './app-commands-list-pane/app-commands-list-pane.component';
import { HttpClientModule } from '@angular/common/http';
import { NgTerminalModule } from 'ng-terminal';

@NgModule({
  declarations: [
    AppComponent,
    AppCommandsListPaneComponent
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
