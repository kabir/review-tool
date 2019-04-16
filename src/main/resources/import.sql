INSERT INTO gh_organisation(id, name, tool_pr_repo) VALUES (1, 'My Project', 'myproject-review')
INSERT INTO gh_organisation(id, name, tool_pr_repo) VALUES (2, 'Overbård', 'overbaard-review')

INSERT INTO gh_user(id, login, name, email, avatar_url) VALUES(1, 'kabir', 'Kabir Khan', 'kkhan@redhat.com', 'https://avatars1.githubusercontent.com/u/49927?v=4')
INSERT INTO gh_user(id, login, name, email, avatar_url) VALUES(2, 'test_user', 'Mock Test User', 'test_user@example.com', 'https://avatars1.githubusercontent.com/u/49928?v=4')
INSERT INTO gh_user(id, login, name, email, avatar_url) VALUES(3, 'non_admin', 'Non Admin', 'non_admin@example.com', 'https://avatars1.githubusercontent.com/u/49929?v=4')

INSERT INTO site_admin(id) VALUES (1);
INSERT INTO site_admin(id) VALUES (2);

INSERT INTO organisation_admin(organisation_id, gh_user_id) VALUES (1, 1)
INSERT INTO organisation_admin(organisation_id, gh_user_id) VALUES (2, 1)

INSERT INTO gh_mirrored_repository(id, organisation_id, upstream_organisation, upstream_repository) VALUES (1, 1, 'up-orgA', 'up-repoA')
INSERT INTO gh_mirrored_repository(id, organisation_id, upstream_organisation, upstream_repository) VALUES (2, 1, 'up-orgB', 'up-repoB')

INSERT INTO review_request(id, title, description, issue_tracker_link, organisation_id, owner_id) VALUES (1, 'First one', 'Blah Blah', 'https://example.com/100', 1, 3)
INSERT INTO review_request(id, title, description, issue_tracker_link, organisation_id, owner_id) VALUES (2, 'Second one', 'Blabla', 'https://example.com/101', 2, 2)

INSERT INTO feature_branch_request(id, owner_id, review_request_id, mirrored_repository_id, feature_branch, target_branch, title) VALUES (1, 1, 2, 2, 'my-branch', 'master', 'Sample Branch Review')