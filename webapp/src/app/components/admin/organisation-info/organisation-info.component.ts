import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ToolApiService} from '../../../services/tool-api.service';
import {Organisation} from '../../../model/organisation';
import {Observable} from 'rxjs';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-organisation-info',
  templateUrl: './organisation-info.component.html',
  styleUrls: ['./organisation-info.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrganisationInfoComponent implements OnInit {

  organisation$: Observable<Organisation>;

  constructor(
    private _apiService: ToolApiService,
    private _route: ActivatedRoute) { }

  ngOnInit() {
    const orgName: string = this._route.snapshot.params['orgId'];
    this.organisation$ = this._apiService.loadOrganisation(orgName);
  }

}
