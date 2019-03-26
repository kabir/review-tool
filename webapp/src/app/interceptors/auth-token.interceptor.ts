import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthTokenService} from '../services/auth-token.service';

@Injectable()
export class AuthTokenInterceptor implements HttpInterceptor {

  constructor(private authTokenService: AuthTokenService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const tokenHeader: string = this.authTokenService.getStoredToken();
    if (tokenHeader) {
      const headers = req.headers ? req.headers : new HttpHeaders();
      req = req.clone({
        headers
      });
    }
    return next.handle(req);
  }
}
