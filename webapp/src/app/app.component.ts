import { Component } from '@angular/core';
import {TOOLBAR_HEIGHT} from './common/view-constants';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'webapp';

  readonly toolbarHeight: number = TOOLBAR_HEIGHT;
}
