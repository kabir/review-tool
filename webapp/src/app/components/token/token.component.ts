import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {Dictionary} from '../../common/dictionary';
import {Observable} from 'rxjs';
import {take} from 'rxjs/operators';
import {AuthTokenService} from '../../services/auth-token.service';

@Component({
  selector: 'app-token',
  templateUrl: './token.component.html',
  styleUrls: ['./token.component.scss']
})
export class TokenComponent implements OnInit {

  error: string;

  constructor(
    private _router: Router,
    private _route: ActivatedRoute,
    private _authTokenService: AuthTokenService) { }

  ngOnInit() {
    const params: Dictionary<string> = this._route.snapshot.queryParams;
    const uuid: string = params['uuid'];
    const encodedPath: string = params['path'];

    console.log(`Received token callback uuid: ${uuid}; path: ${encodedPath}`);

    const error$: Observable<string> = this._authTokenService.exchangeForToken(uuid);
    error$
      .pipe(
        take(1)
      )
      .subscribe(
        value => {
          if (value) {
            this.error = value;
          } else {

            let path = '/';
            const queryParams = {};
            if (encodedPath && encodedPath.length > 0) {
              const fullPath = decodeURIComponent(encodedPath);
              console.log('decoded path' + fullPath);
              const index: number = fullPath.indexOf('?');
              if (index < 0) {
                path = fullPath;
              } else {
                path = fullPath.substring(0, index);
                const query: string = fullPath.substring(index + 1);
                const values: string[] = query.split('&');
                for (const curr of values) {
                  const pair = curr.split('=');
                  queryParams[pair[0]] = pair[1];
                }
              }
            }

            this._router.navigate([path], {queryParams});
          }
        }
      );
  }
}

class RouterParams {
  path: string;
  queryParams: object;
}
