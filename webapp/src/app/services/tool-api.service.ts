import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, subscribeOn, take, timeout} from 'rxjs/operators';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ToolApiService {

  constructor(private _http: HttpClient) { }

  loadAllOrganisations(): Observable<any> {
    return this._http.get('/api/config/organisations', {
      headers: new HttpHeaders().append('Content-Type', 'application/json')
    });
  }
}
