import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {TestAComponent} from './test-a/test-a.component';
import {TestBComponent} from './test-b/test-b.component';

const routes: Routes = [
  {
    path: '', redirectTo: '/a', pathMatch: 'full'
  },
  {
    path: 'a', component: TestAComponent
  },
  {
    path: 'b', component: TestBComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
