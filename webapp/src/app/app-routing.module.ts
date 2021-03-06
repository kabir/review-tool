import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {TokenComponent} from './components/token/token.component';
import {OrganisationComponent} from './components/organisation/organisation.component';
import {OrganisationHomeComponent} from './components/organisation/organisation-home/organisation-home.component';
import {AdminComponent} from './components/admin/admin.component';
import {AdminGuard} from './guards/admin.guard';
import {AdminHomeComponent} from './components/admin/admin-home/admin-home.component';
import {OrganisationInfoComponent} from './components/admin/organisation-info/organisation-info.component';
import {OrganisationInfoViewComponent} from './components/admin/organisation-info/view/organisation-info-view.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: HomeComponent
  },
  {
    path: 'token',
    component: TokenComponent
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AdminGuard],
    children: [
      {
        path: '',
        component: AdminHomeComponent
      },
      {
        path: 'organisation',
        component: OrganisationInfoComponent
      },
      {
        path: 'organisation/:orgId',
        component: OrganisationInfoComponent
      }
    ]
  },
  {
    path: 'organisation/:orgId',
    component: OrganisationComponent,
    children: [
      {
        path: '',
        component: OrganisationHomeComponent}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
