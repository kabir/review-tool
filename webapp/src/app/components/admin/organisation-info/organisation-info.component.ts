import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ToolApiService} from '../../../services/tool-api.service';
import {Organisation} from '../../../model/organisation';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {take} from 'rxjs/operators';
import {MirroredRepository} from '../../../model/MirroredRepository';

@Component({
  selector: 'app-organisation-info',
  templateUrl: './organisation-info.component.html',
  styleUrls: ['./organisation-info.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrganisationInfoComponent implements OnInit {

  organisation$: Subject<Organisation> = new BehaviorSubject(null);

  private _orgId: number;

  constructor(
    private _apiService: ToolApiService,
    private _route: ActivatedRoute,
    private _router: Router) { }

  ngOnInit() {
    this._orgId = this._route.snapshot.params['orgId'];
    if (this._orgId) {
      this.loadOrganisation();
    }
  }

  private loadOrganisation() {
    this._apiService.loadOrganisation(this._orgId)
      .pipe(
        take(1)
      )
      .subscribe(
        // TODO Handle errors
        org => this.organisation$.next(org)
      );
  }

  onModifiedOrganisation(org: Organisation) {
    this._apiService.saveOrganisation(org)
      .subscribe(
        updatedOrg => {
          this.organisation$.next(updatedOrg);
          if (!org.id) {
            this._router.navigate(['admin', 'organisation', updatedOrg.id]);
          }
        }
      );

  }

  onModifiedRepository(repo: MirroredRepository) {
    // _orgId will always be set for this path (we can't add/modify repos when creating an org)
    this._apiService.saveRepository(this._orgId, repo)
      .pipe(
        take(1)
      )
      .subscribe(
        updatedRepo => {
          this.loadOrganisation();
        }
      );
  }
}
