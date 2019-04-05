import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ToolApiService} from '../../../services/tool-api.service';
import {Observable} from 'rxjs';
import {Organisation} from '../../../model/organisation';
import {Router} from '@angular/router';

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminHomeComponent implements OnInit {

  organisations$: Observable<Organisation[]>;

  constructor(private _apiService: ToolApiService,
              private _router: Router) {
  }

  ngOnInit() {
    this.organisations$ = this._apiService
      .loadAllOrganisations();
  }

  onCreateOrganisation() {
    this._router.navigate(['admin', 'organisation']);
  }
}
