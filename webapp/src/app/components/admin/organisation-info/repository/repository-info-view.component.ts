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
  selector: 'app-repository-info-view',
  templateUrl: './repository-info-view.component.html',
  styleUrls: ['./repository-info-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RepositoryInfoViewComponent implements OnInit, OnChanges {

  @Input()
  organisation: Organisation;

  @Input()
  repository: MirroredRepository;

  repoForm: FormGroup;

  @Output()
  modifiedRepository: EventEmitter<MirroredRepository> = new EventEmitter<MirroredRepository>();
  private create: boolean;
  private _editRepo: boolean;

  constructor() {

  }

  ngOnInit() {
    this.setUpRepositoryForm(this.repository);
  }

  ngOnChanges(changes: SimpleChanges): void {
    const repoChange: SimpleChange = changes['repository'];
    if (repoChange) {
      if (repoChange.previousValue !== repoChange.currentValue && repoChange.currentValue) {
        const repo: MirroredRepository = repoChange.currentValue;
        this.setUpRepositoryForm(repo);
      }
    }
  }

  private setUpRepositoryForm(repository: MirroredRepository) {
    if (!this.repository) {
      this.create = true;
      this._editRepo = true;
    } else {
      this.create = false;
      this._editRepo = false;
    }

    this.repoForm = new FormGroup({});
    this.repoForm.addControl('upstreamOrganisation',
      new FormControl(repository ? repository.upstreamOrganisation : '', Validators.required));
    this.repoForm.addControl('upstreamRepository',
      new FormControl(repository ? repository.upstreamRepository : '', Validators.required));
    if (!this._editRepo) {
      this.repoForm.disable();
    }
  }

  get editRepo(): boolean {
    return this._editRepo;
  }

  set editRepo(value: boolean) {
    this._editRepo = value;
    if (!this._editRepo) {
      this.repoForm.disable();
    } else {
      this.repoForm.enable();
    }
  }

  onCancelRepo(event: MouseEvent) {
    event.preventDefault();
    this.editRepo = false;
  }

  canSaveRepo() {
    return this.repoForm.dirty && this.repoForm.valid;
  }

  onSaveRepo() {
    event.preventDefault();
    const id: number = this.repository ? this.repository.id : null;
    const repo: MirroredRepository = {
      id,
      upstreamOrganisation: this.repoForm.controls['upstreamOrganisation'].value,
      upstreamRepository: this.repoForm.controls['upstreamRepository'].value
    };

    this.modifiedRepository.emit(repo);

  }
}
