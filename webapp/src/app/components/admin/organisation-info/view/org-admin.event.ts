export interface OrgAdminEvent {
  type: OrgAdminEventType;
  login: string;
}

export enum OrgAdminEventType {
  ADD,
  DELETE
}
