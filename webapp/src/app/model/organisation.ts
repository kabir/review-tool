import {MirroredRepository} from './MirroredRepository';

export interface Organisation {
  id: number;
  name: string;
  toolPrRepo: string;
  mirroredRepositories: MirroredRepository[];
}

export class OrganisationUtils {
  static getGitHubUrl(org: Organisation) {
    return `https://github.com/${org.name}/${org.toolPrRepo}`;
  }
}
