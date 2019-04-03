import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, subscribeOn, take, tap, timeout} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {Organisation} from '../model/organisation';
import {keysToCamel} from '../common/snake-to-camel-case';

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
        tap(v => console.log(v)),
        map(v => keysToCamel(v))
      );
  }

  loadOrganisation(orgName: string): Observable<Organisation> {
    return this._http.get<Organisation[]>('/api/config/organisations/' + orgName, {
      headers: new HttpHeaders().append('Content-Type', 'application/json')
    })
      .pipe(
        take(1),
        timeout(60000),
        tap(v => console.log(v)),
        map(v => keysToCamel(v))
      );
  }
}
