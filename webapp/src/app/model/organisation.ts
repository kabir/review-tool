import {MirroredRepository} from './MirroredRepository';
import {User} from './user';

export interface Organisation {
  id: number;
  name: string;
  toolPrRepo: string;
  mirroredRepositories: MirroredRepository[];
  admins: User[];
}

export class OrganisationUtils {
  static getGitHubUrl(org: Organisation) {
    return `https://github.com/${org.name}/${org.toolPrRepo}`;
  }
}
