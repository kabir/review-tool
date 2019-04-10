import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {take, tap, timeout} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {Organisation} from '../model/organisation';
import {MirroredRepository} from '../model/MirroredRepository';
import {User} from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class ToolApiService {

  constructor(private _http: HttpClient) { }

  loadAllOrganisations(): Observable<Organisation[]> {
    return this._http.get<Organisation[]>('/api/config/organisations', {headers})
      .pipe(
        take(1),
        timeout(60000),
        tap(v => console.log(v))
      );
  }

  loadOrganisation(orgId: number): Observable<Organisation> {
    return this._http.get<Organisation>(`/api/config/organisations/${orgId}`, {headers})
      .pipe(
        take(1),
        timeout(60000)
      );
  }

  saveOrganisation(organisation: Organisation): Observable<Organisation> {
    let observable: Observable<Organisation>;
    if (organisation.id) {
      observable = this._http.put<Organisation>(`/api/config/organisations/${organisation.id}`, organisation, {headers});
    } else {
      observable = this._http.post<Organisation>('/api/config/organisations', organisation, {headers});
    }
    return observable
      .pipe(
        take(1),
        timeout(60000),
      );
  }

  saveRepository(orgId: number, repo: MirroredRepository) {
    let observable: Observable<Organisation>;
    if (repo.id) {
      observable = this._http.put<Organisation>(`/api/config/organisations/${orgId}/repositories/${repo.id}`, repo, {headers});
    } else {
      observable = this._http.post<Organisation>(`/api/config/organisations/${orgId}/repositories`, repo, {headers});
    }
    return observable
      .pipe(
        take(1),
        timeout(60000),
      );
  }

  loadAllSiteAdmins(): Observable<User[]> {
    return this._http.get<User[]>('/api/auth/siteAdmin', {headers})
      .pipe(
        take(1),
        timeout(60000),
        tap(v => console.log(v))
      );
  }

  setSiteAdmin(login: string, admin: boolean): Observable<object> {
    return this._http.put(`/api/auth/siteAdmin/${login}`, {value: admin}, {headers})
      .pipe(
        take(1),
        timeout(60000)
      );
  }


  addOrgAdmin(orgId: number, login: string): Observable<object> {
    return this._http.post(`/api/auth/organisation/${orgId}/admin/${login}`, {}, {headers})
      .pipe(
        take(1),
        timeout(60000),
      );
  }

  deleteOrgAdmin(orgId: number, login: string): Observable<object> {
    return this._http.delete(`/api/auth/organisation/${orgId}/admin/${login}`, {headers})
      .pipe(
        take(1),
        timeout(60000),
      );
  }
}

const headers: HttpHeaders = new HttpHeaders().append('Content-Type', 'application/json');

