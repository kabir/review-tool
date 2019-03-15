import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {TestAComponent} from './test-a/test-a.component';
import {TestBComponent} from './test-b/test-b.component';
import { TestCComponent } from './test-c/test-c.component';

@NgModule({
  declarations: [
    AppComponent,
    TestAComponent,
    TestBComponent,
    TestCComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
