import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChange,
  SimpleChanges
} from '@angular/core';
import {Organisation} from '../../../../model/organisation';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MirroredRepository} from '../../../../model/MirroredRepository';
import {User} from '../../../../model/user';
import {OrgAdminEvent, OrgAdminEventType} from './org-admin.event';

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
  private _editOrg = false;

  orgForm: FormGroup;

  @Output()
  modifiedOrganisation: EventEmitter<Organisation> = new EventEmitter<Organisation>();


  @Output()
  modifiedRepository: EventEmitter<MirroredRepository> = new EventEmitter<MirroredRepository>();

  @Output()
  admin: EventEmitter<OrgAdminEvent> = new EventEmitter<OrgAdminEvent>();

  newAdminForm: FormGroup;
  private _newAdminField: FormControl;

  constructor() {

  }

  ngOnInit() {
    this.setupOrgForm(this.organisation);
    this.setupAdminForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const orgChange: SimpleChange = changes['organisation'];
    if (orgChange) {
      if (orgChange.previousValue !== orgChange.currentValue && orgChange.currentValue) {
        const org: Organisation = orgChange.currentValue;
        this.setupOrgForm(org);
        this.setupAdminForm();
      }
    }
  }

  get editOrg(): boolean {
    return this._editOrg;
  }

  set editOrg(value: boolean) {
    this._editOrg = value;
    if (!this._editOrg) {
      this.orgForm.disable();
    } else {
      this.orgForm.enable();
    }
  }

  private setupOrgForm(organisation: Organisation) {
    if (!this.organisation) {
      this.create = true;
      this._editOrg = true;
    } else {
      this.create = false;
      this._editOrg = false;
    }

    this.orgForm = new FormGroup({});
    this.orgForm.addControl('name',
      new FormControl(organisation ? organisation.name : '', Validators.required));
    this.orgForm.addControl('toolPrRepo',
      new FormControl(organisation ? organisation.toolPrRepo : '', Validators.required));
    if (!this.editOrg) {
      this.orgForm.disable();
    }
  }

  private setupAdminForm() {
    this.newAdminForm = new FormGroup({});
    this._newAdminField = new FormControl('', Validators.required);
    this.newAdminForm.addControl('newAdmin', this._newAdminField);
  }

  canSaveOrg(): boolean {
    return this.orgForm.dirty && this.orgForm.valid;
  }

  onCancelOrg(event: MouseEvent) {
    this.editOrg = false;
  }


  onSaveOrg() {
    event.preventDefault();
    const id: number = this.organisation ? this.organisation.id : null;
    const org: Organisation = {
      id,
      name: this.orgForm.controls['name'].value,
      toolPrRepo: this.orgForm.controls['toolPrRepo'].value,
      mirroredRepositories: null,
      admins: null
    };

    this.modifiedOrganisation.emit(org);
  }

  onModifiedRepository(repo: MirroredRepository) {
    this.modifiedRepository.emit(repo);
  }

  onAddOrgAdmin() {
    this.admin.emit({type: OrgAdminEventType.ADD, login: this._newAdminField.value});
  }

  onDeleteOrgAdmin(admin: User) {
    this.admin.emit({type: OrgAdminEventType.DELETE, login: admin.login});
  }

  canSaveAdmin() {
    return this.newAdminForm.dirty && this.newAdminForm.valid;
  }
}
