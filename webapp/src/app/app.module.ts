import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModule} from './material.module';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {HomeComponent} from './components/home/home.component';
import {TokenComponent} from './components/token/token.component';
import {AuthTokenService} from './services/auth-token.service';
import {AuthTokenInterceptor} from './interceptors/auth-token.interceptor';
import { OrganisationComponent } from './components/organisation/organisation.component';
import { AdminComponent } from './components/admin/admin.component';
import { AdminHomeComponent } from './components/admin/admin-home/admin-home.component';
import {OrganisationHomeComponent} from './components/organisation/organisation-home/organisation-home.component';
import { OrganisationInfoComponent } from './components/admin/organisation-info/organisation-info.component';
import { OrganisationInfoViewComponent } from './components/admin/organisation-info/view/organisation-info-view.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';


@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    TokenComponent,
    OrganisationComponent,
    OrganisationHomeComponent,
    AdminComponent,
    AdminHomeComponent,
    OrganisationInfoComponent,
    OrganisationInfoViewComponent
  ],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    MaterialModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: [
    AuthTokenService,
    {provide: HTTP_INTERCEPTORS, useClass: AuthTokenInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
