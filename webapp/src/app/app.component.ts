import {Component, OnInit} from '@angular/core';
import {TOOLBAR_HEIGHT} from './common/view-constants';
import {AuthTokenService} from './services/auth-token.service';
import {ActivatedRoute} from '@angular/router';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {ToolApiService} from './services/tool-api.service';
import {User} from './model/user';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'webapp';

  readonly toolbarHeight: number = TOOLBAR_HEIGHT


  constructor(
    public authTokenService: AuthTokenService,
    private _toolApi: ToolApiService,
    private _route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.authTokenService.onFirstLoad();
  }

  onLogout(event: MouseEvent) {
    event.preventDefault();
    this.authTokenService.logOut();
  }

  onLogin(event: MouseEvent) {
    event.preventDefault();
    this.authTokenService.logIn();
  }
}
