INSERT INTO gh_organisation(id, name, tool_pr_repo) VALUES (1, 'My Project', 'myproject-review')
INSERT INTO gh_organisation(id, name, tool_pr_repo) VALUES (2, 'Overbård', 'overbaard-review')

INSERT INTO gh_user(id, login, name, avatar_url, site_admin) VALUES(1, 'kabir', 'Kabir Khan', 'https://avatars1.githubusercontent.com/u/49927?v=4', true)

INSERT INTO gh_mirrored_repository(id, organisation_id, upstream_organisation, upstream_repository) VALUES (1, 1, 'up-orgA', 'up-repoA')
INSERT INTO gh_mirrored_repository(id, organisation_id, upstream_organisation, upstream_repository) VALUES (2, 1, 'up-orgB', 'up-repoB')
