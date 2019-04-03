import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChange, SimpleChanges} from '@angular/core';
import {Organisation} from '../../../../model/organisation';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ToolApiService} from '../../../../services/tool-api.service';

@Component({
  selector: 'app-organisation-info-view',
  templateUrl: './organisation-info-view.component.html',
  styleUrls: ['./organisation-info-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrganisationInfoViewComponent implements OnInit, OnChanges {

  @Input()
  organisation: Organisation;

  create = false;
  private _edit = false;

  orgForm: FormGroup;

  constructor(
    private _apiService: ToolApiService) {

  }

  ngOnInit() {
    this.setUpOrgForm(this.organisation);
  }

  ngOnChanges(changes: SimpleChanges): void {
    const orgChange: SimpleChange = changes['organisation'];
    if (orgChange) {
      if (orgChange.previousValue !== orgChange.currentValue && orgChange.currentValue) {
        const org: Organisation = orgChange.currentValue;
        this.setUpOrgForm(org);
      }
    }
  }

  get edit(): boolean {
    return this._edit;
  }

  set edit(value: boolean) {
    this._edit = value;
    if (!this._edit) {
      this.orgForm.disable();
    } else {
      this.orgForm.enable();
    }
  }

  private setUpOrgForm(organisation: Organisation) {
    if (!this.organisation) {
      this.create = true;
      this._edit = true;
    } else {
      this.create = false;
      this._edit = false;
    }

    const tmp: FormControl = new FormControl('x');
    this.orgForm = new FormGroup({});
    this.orgForm.addControl('name', new FormControl(organisation ? organisation.name : '', Validators.required));
    this.orgForm.addControl('toolPrRepo', new FormControl(organisation ? organisation.toolPrRepo : '', Validators.required));
    if (!this.edit) {
      this.orgForm.disable();
    }
    console.log(this.orgForm.value);
  }

  canSaveOrg(): boolean {
    return this.orgForm.dirty && this.orgForm.valid;
  }

  onCancel(event: MouseEvent) {

  }


  onSave() {
    event.preventDefault();
    const org: Organisation = {
      id: this.organisation.id ? this.organisation.id : -1,
      name: this.orgForm['name'],
      toolPrRepo: this.orgForm['toolPrRepo'].value,
    };
    this._apiService.saveOrganisation(org);
  }
}
