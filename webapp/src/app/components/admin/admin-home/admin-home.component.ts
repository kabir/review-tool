import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ToolApiService} from '../../../services/tool-api.service';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {Organisation} from '../../../model/organisation';
import {Router} from '@angular/router';
import {User} from '../../../model/user';
import {take} from 'rxjs/operators';
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminHomeComponent implements OnInit {

  organisations$: Observable<Organisation[]>;

  siteAdmins$: Subject<User[]> = new BehaviorSubject<User[]>([]);

  newAdminForm: FormGroup;
  private _newAdminField: FormControl;

  constructor(private _apiService: ToolApiService,
              private _router: Router) {
  }

  ngOnInit() {
    this.organisations$ = this._apiService
      .loadAllOrganisations();

    this.loadSiteAdmins();

    this.newAdminForm = new FormGroup({});
    this._newAdminField = new FormControl('', Validators.required);
    this.newAdminForm.addControl('newAdmin', this._newAdminField);
  }

  private loadSiteAdmins() {
    this._apiService
      .loadAllSiteAdmins()
      .pipe(
        take(1)
      )
      .subscribe(
        value => this.siteAdmins$.next(value)
      );
  }

  onCreateOrganisation() {
    this._router.navigate(['admin', 'organisation']);
  }

  onDeleteSiteAdmin(admin: User) {
    this.changeSiteAdmin(admin.login, false);
  }

  onAddSiteAdmin() {
    this.changeSiteAdmin(
      this._newAdminField.value,
      true,
      () => {
        this._newAdminField.reset('');
      });
  }

  private changeSiteAdmin(login: string, admin: boolean, callback?: () => void) {
    this._apiService.setSiteAdmin(login, admin)
      .subscribe(
        value => {
          this.loadSiteAdmins();
          if (callback) {
            callback();
          }
        }
      );
  }

  canSaveAdmin(): boolean {
    return this.newAdminForm.dirty && this.newAdminForm.valid;
  }
}
