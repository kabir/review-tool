import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {catchError, take, timeout} from 'rxjs/operators';

import {BehaviorSubject, Observable, Subject, Subscription, throwError, timer} from 'rxjs';
import {environment} from '../../environments/environment';
import {User} from '../model/user';

const HAS_BEEN_LOGGED_IN = 'overbaard.review.tool.has-been-logged-in';

@Injectable()
export class AuthTokenService {

  private _tokenHeader: string;
  private _loggedIn: BehaviorSubject<User> = new BehaviorSubject<User>(null);
  private _siteAdmin$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  private _proxy: boolean;
  private _origin: string;

  constructor(private _http: HttpClient) {
    this._proxy = environment.proxy;
    this._origin = window.location.origin;
  }

  get loggedInUser$(): Observable<User> {
    return this._loggedIn;
  }

  private setLoggedInUser(loggedIn: User) {
    this._loggedIn.next(loggedIn);
  }

  get siteAdmin$(): BehaviorSubject<boolean> {
    return this._siteAdmin$;
  }

  exchangeForToken(uuid: string): Observable<string> {
    const returnObservable$: Subject<string> = new Subject<string>();
    this._http.post<TokenResponse>(
      '/auth/exchange',
      {uuid},
      {
        headers: new HttpHeaders().append('Content-Type', 'application/json')
      })
      .pipe(
        take(1),
        timeout(60000),
        catchError(err => this.handleError<TokenResponse>(returnObservable$, err))
      )
      .subscribe(
        (value: TokenResponse) => {
          console.log('Exchanged uuid for token');
          returnObservable$.next(null);
          if (value.tokenHeader) {
            // window.sessionStorage is not safe, just store it in memory
            this._tokenHeader = value.tokenHeader;
            // Store that we have been logged in at some stage
            window.localStorage.setItem(HAS_BEEN_LOGGED_IN, 'true');
            this.setLoggedInUser(value.user);
            this.siteAdmin$.next(value.siteAdmin);
            this.initialiseAdminPolling();
          }
        });
    return returnObservable$;
  }



  private initialiseAdminPolling() {
    if (environment.proxy) {
      // Turn this off for now
      return;
    }
    const subscription: Subscription = timer(0, 30000)
      .subscribe(
        value => {
          let loggedIn = false;
          this.loggedInUser$.subscribe(user => {
            loggedIn = !!user;
          });
          if (!loggedIn) {
            subscription.unsubscribe();
            this._siteAdmin$.next(false);
            return;
          }
          this._http.get<any>(
              '/api/auth/admin', {
                headers: new HttpHeaders().append('Content-Type', 'application/json')
              })
            .pipe(
              take(1),
              timeout(60000),
              catchError(err => {
                this._siteAdmin$.next(false);
                throw new Error();
              })
            )
            .subscribe(
              adminResponse => {
                this._siteAdmin$.next(adminResponse.admin);
              }
            );
        }
      );
  }

  getStoredToken(): string {
    return this._tokenHeader;
  }

  logOut() {
    this._tokenHeader = null;
    window.localStorage.removeItem(HAS_BEEN_LOGGED_IN);
    this.siteAdmin$.next(false);
    this.setLoggedInUser(null);
  }

  logIn() {
    const path = location.href.substring(location.origin.length);
    const encodedPath = encodeURIComponent(path);
    let href = '/auth/login?path=' + encodedPath;
    if (this._proxy) {
      href += '&proxy.url=' + encodeURIComponent(this._origin);
    }
    console.log('Going to login at: ' + href);
    window.location.href = href;
  }

  onFirstLoad() {
    if (this._tokenHeader) {
      return;
    }
    const hasLoggedIn: boolean = Boolean(window.localStorage.getItem(HAS_BEEN_LOGGED_IN));
    // We have logged in before, so initiate login. As this directs away from this page, and comes back into the
    // app as a new request if we were all logged in, it will be hard to know if it worked or not so let's keep it simple for now.
    if (!hasLoggedIn) {
      this.setLoggedInUser(null);
    } else {
      // Remove the logged in marker to avoid looping around, and try logging in
      window.localStorage.removeItem(HAS_BEEN_LOGGED_IN);
      this.logIn();
    }
  }


  handleError<T>(returnObservable$: Subject<string>, error: HttpErrorResponse): Observable<T> {

    let msg: string;

    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      msg = 'An error occurred: ' + error.error.message;
      console.error(msg);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      msg = `Backend returned code ${error.status}, body was: ${error.error}`;
      console.error(msg);
    }

    if (returnObservable$) {
      returnObservable$.next(`We were not able to authenticate you at this time.\n${msg}`);
    }
    // return an observable with a user-facing error message
    return throwError(
      'We were not able to authenticate you at this time');

  }
}

interface TokenResponse {
  tokenHeader: string;
  siteAdmin: boolean;
  user: User;
}

interface AdminResponse {
  siteAdmin: boolean;
}
