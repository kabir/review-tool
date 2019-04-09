INSERT INTO gh_organisation(id, name, tool_pr_repo) VALUES (1, 'My Project', 'myproject-review')
INSERT INTO gh_organisation(id, name, tool_pr_repo) VALUES (2, 'Overbård', 'overbaard-review')

INSERT INTO gh_user(id, login, name, email, avatar_url) VALUES(1, 'kabir', 'Kabir Khan', 'kkhan@redhat.com', 'https://avatars1.githubusercontent.com/u/49927?v=4')
INSERT INTO gh_user(id, login, name, email, avatar_url) VALUES(2, 'test_user', 'Mock Test User', 'test_user@example.com', 'https://avatars1.githubusercontent.com/u/49928?v=4')
INSERT INTO gh_user(id, login, name, email, avatar_url) VALUES(3, 'non_admin', 'Non Admin', 'non_admin@example.com', 'https://avatars1.githubusercontent.com/u/49929?v=4')

INSERT INTO site_admin(id) VALUES (1);
INSERT INTO site_admin(id) VALUES (2);

INSERT INTO gh_mirrored_repository(id, organisation_id, upstream_organisation, upstream_repository) VALUES (1, 1, 'up-orgA', 'up-repoA')
INSERT INTO gh_mirrored_repository(id, organisation_id, upstream_organisation, upstream_repository) VALUES (2, 1, 'up-orgB', 'up-repoB')
