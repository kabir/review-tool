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
  private _edit: boolean;

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
      this._edit = true;
    } else {
      this.create = false;
      this._edit = false;
    }

    this.repoForm = new FormGroup({});
    this.repoForm.addControl('upstreamOrganisation',
      new FormControl(repository ? repository.upstreamOrganisation : '', Validators.required));
    this.repoForm.addControl('upstreamRepository',
      new FormControl(repository ? repository.upstreamRepository : '', Validators.required));
    if (!this._edit) {
      this.repoForm.disable();
    }
  }

  get edit(): boolean {
    return this._edit;
  }

  set edit(value: boolean) {
    this._edit = value;
    if (!this._edit) {
      this.repoForm.disable();
    } else {
      this.repoForm.enable();
    }
  }

  onCancelRepo(event: MouseEvent) {
    event.preventDefault();
    this.edit = false;
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
