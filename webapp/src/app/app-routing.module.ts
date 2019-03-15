import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {TestAComponent} from './test-a/test-a.component';
import {TestBComponent} from './test-b/test-b.component';
import {TestCComponent} from './test-c/test-c.component';

const routes: Routes = [
  {
    path: '', redirectTo: '/a', pathMatch: 'full'
  },
  {
    path: 'a', component: TestAComponent
  },
  {
    path: 'b', component: TestBComponent
  },
  {
    path: 'c', component: TestCComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
