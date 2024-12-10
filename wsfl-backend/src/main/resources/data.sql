-- Clear existing data
TRUNCATE TABLE users, leagues, teams, runners, races, race_results, matchups, seasons, user_roles, drafts, draft_order, draft_picks CASCADE;

-- Insert Users with hashed passwords (password is 'password')
INSERT INTO users (id, email, name, picture, active, password) VALUES
(1, 'tbeerbower@gmail.com', 'Tom B', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS'),
(2, 'jane.smith@example.com', 'Jane Smith', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS'),
(3, 'bob.wilson@example.com', 'Bob Wilson', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS'),
(4, 'alice.jones@example.com', 'Alice Jones', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS'),
(5, 'john.doe@example.com', 'John Doe', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS'),
(6, 'kate@example.com', 'Kate Spate', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS');

-- Insert User Roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_USER'),
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_USER'),
(4, 'ROLE_USER'),
(5, 'ROLE_USER'),
(6, 'ROLE_USER');


-- Reset sequence values
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('leagues_id_seq', (SELECT MAX(id) FROM leagues));
SELECT setval('teams_id_seq', (SELECT MAX(id) FROM teams));
SELECT setval('runners_id_seq', (SELECT MAX(id) FROM runners));
SELECT setval('races_id_seq', (SELECT MAX(id) FROM races));
SELECT setval('race_results_id_seq', (SELECT MAX(id) FROM race_results));
SELECT setval('matchups_id_seq', (SELECT MAX(id) FROM matchups));
SELECT setval('drafts_id_seq', (SELECT MAX(id) FROM drafts));
SELECT setval('draft_picks_id_seq', (SELECT MAX(id) FROM draft_picks));