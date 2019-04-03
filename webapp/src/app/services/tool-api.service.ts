import {Injectable} from '@angular/core';
import {HttpClient, HttpEvent, HttpHeaders} from '@angular/common/http';
import {take, tap, timeout} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {Organisation} from '../model/organisation';

@Injectable({
  providedIn: 'root'
})
export class ToolApiService {

  constructor(private _http: HttpClient) { }

  loadAllOrganisations(): Observable<Organisation[]> {
    return this._http.get<Organisation[]>('/api/config/organisations', {
      headers: new HttpHeaders().append('Content-Type', 'application/json')
    })
      .pipe(
        take(1),
        timeout(60000),
        tap(v => console.log(v))
      );
  }

  loadOrganisation(orgName: string): Observable<Organisation> {
    return this._http.get<Organisation>('/api/config/organisations/' + orgName, {
      headers: new HttpHeaders().append('Content-Type', 'application/json')
    })
      .pipe(
        take(1),
        timeout(60000)
      );
  }

  saveOrganisation(organisation: Organisation) {
    const options: any = {
      headers: new HttpHeaders().append('Content-Type', 'application/json')
    };
    let observable: Observable<HttpEvent<Organisation>>;
    if (organisation.id >= 0) {
      observable = this._http.put<Organisation>('/api/config/organisations/' + organisation.id, organisation, options);
    } else {
      observable = this._http.post<Organisation>('/api/config/organisations', organisation, options);
    }
    return observable
      .pipe(
        take(1),
        timeout(60000),
      );
  }
}
