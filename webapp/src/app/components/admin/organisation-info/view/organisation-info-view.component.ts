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

  activeRepository: MirroredRepository;
  repoForm: FormGroup;

  private _editRepository: boolean;

  @Output()
  modifiedRepository: EventEmitter<MirroredRepository> = new EventEmitter<MirroredRepository>();

  constructor() {

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

  private setUpOrgForm(organisation: Organisation) {
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

  canEditOrg(): boolean {
    return !this.activeRepository;
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
      mirroredRepositories: null
    };

    this.modifiedOrganisation.emit(org);
  }

  get editRepository(): boolean {
    return this._editRepository;
  }

  set editRepository(value: boolean) {
    this._editRepository = value;
    if (!this._editRepository) {
      this.repoForm.disable();
    } else {
      this.repoForm.enable();
    }
  }

  onOpenRepository(repo: MirroredRepository) {
    this.activeRepository = repo;
    this.repoForm = new FormGroup({});
    this.repoForm.addControl('upstreamOrganisation',
      new FormControl(repo ? repo.upstreamOrganisation : '', Validators.required));
    this.repoForm.addControl('upstreamRepository',
      new FormControl(repo ? repo.upstreamRepository : '', Validators.required));
    this.editRepository = false;
  }

  onCloseRepository(repo: MirroredRepository) {
    if (this.activeRepository === repo) {
      this.activeRepository = null;
    }
  }

  onCancelRepo(event: MouseEvent) {
    event.preventDefault();
    this.editRepository = false;
  }

  canSaveRepo() {
    return this.repoForm.dirty && this.repoForm.valid;
  }

  onSaveRepo() {
    event.preventDefault();
    const id: number = this.activeRepository ? this.activeRepository.id : null;
    const repo: MirroredRepository = {
      id,
      upstreamOrganisation: this.repoForm.controls['upstreamOrganisation'].value,
      upstreamRepository: this.repoForm.controls['upstreamRepository'].value
    };

    this.modifiedRepository.emit(repo);

  }
}
