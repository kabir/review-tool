import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {catchError, map, take, timeout} from 'rxjs/operators';
import {BehaviorSubject, Observable, Subject, throwError} from 'rxjs';

const HAS_BEEN_LOGGED_IN = 'overbaard.review.tool.has-been-logged-in';

@Injectable()
export class AuthTokenService {

  private _tokenHeader: string;
  private _loggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(private http: HttpClient) {
  }

  get loggedIn$(): Observable<boolean> {
    return this._loggedIn;
  }

  private setLoggedIn(loggedIn: boolean) {
    this._loggedIn.next(loggedIn);
  }

  exchangeForToken(uuid: string): Observable<string> {
    const returnObservable$: Subject<string> = new Subject<string>();
    this.http.post<TokenResponse>(
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
        value => {
          console.log('Exchanged uuid for token');
          returnObservable$.next(null);
          // window.sessionStorage is not safe, just store it in memory
          this._tokenHeader = value.tokenHeader;
          // Store that we have been logged in at some stage
          window.localStorage.setItem(HAS_BEEN_LOGGED_IN, 'true');
          this.setLoggedIn(true);
        });
    return returnObservable$;
  }

  getStoredToken(): string {
    return this._tokenHeader;
  }

  logOut() {
    this._tokenHeader = null;
    window.localStorage.removeItem(HAS_BEEN_LOGGED_IN);
    this.setLoggedIn(false);
  }

  logIn() {
    const path = location.href.substring(location.origin.length);
    const encodedPath = encodeURIComponent(path);
    const href = '/auth/login?path=' + encodedPath;
    console.log('Going to login at: ' + href);
    window.location.href = href;
  }

  onFirstLoad() {
    const hasLoggedIn: boolean = Boolean(window.localStorage.getItem(HAS_BEEN_LOGGED_IN));
    // We have logged in before, so initiate login. As this directs away from this page, and comes back into the
    // app as a new request if we were all logged in, it will be hard to know if it worked or not so let's keep it simple for now.
    if (!hasLoggedIn) {
      this.setLoggedIn(false);
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

    returnObservable$.next(`We were not able to authenticate you at this time.\n${msg}`);
    // return an observable with a user-facing error message
    return throwError(
      'We were not able to authenticate you at this time');

  }
}

interface TokenResponse {
  tokenHeader: string;
}
