-- Clear existing data
TRUNCATE TABLE users, leagues, teams, runners, races, race_results, matchups, team_runners, user_roles, drafts, draft_order, draft_picks CASCADE;

-- Insert Users with hashed passwords (password is 'password')
INSERT INTO users (id, email, name, picture, active, password) VALUES
(1, 'tbeerbower@gmail.com', 'Tom B', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS'),
(2, 'jane.smith@example.com', 'Jane Smith', '', true, '$2a$10$rS.rJzP0ZHqq2hV3HDzpBeMQOGA0FWyoCxjWxvVHrfqVGfCJrqY6m'),
(3, 'bob.wilson@example.com', 'Bob Wilson', '', true, '$2a$10$rS.rJzP0ZHqq2hV3HDzpBeMQOGA0FWyoCxjWxvVHrfqVGfCJrqY6m'),
(4, 'alice.jones@example.com', 'Alice Jones', '', true, '$2a$10$rS.rJzP0ZHqq2hV3HDzpBeMQOGA0FWyoCxjWxvVHrfqVGfCJrqY6m'),
(5, 'john.doe@example.com', 'John Doe', '', true, '$2a$10$rS.rJzP0ZHqq2hV3HDzpBeMQOGA0FWyoCxjWxvVHrfqVGfCJrqY6m');

-- Insert User Roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_USER'),
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_USER'),
(4, 'ROLE_USER'),
(5, 'ROLE_USER');

-- Insert Leagues
INSERT INTO leagues (id, name, season, max_teams, admin_id) VALUES
(1, 'Summer League 2024', 2024, 4, 5),
(2, 'Winter League 2024', 2024, 4, 5);

-- Insert Teams
INSERT INTO teams (id, name, wins, losses, ties, total_score, owner_id, league_id) VALUES
(1, 'Speed Demons', 2, 1, 0, 45, 1, 1),
(2, 'Trail Blazers', 1, 2, 0, 52, 2, 1),
(3, 'Road Warriors', 2, 0, 1, 38, 3, 1),
(4, 'Hill Climbers', 0, 2, 1, 58, 4, 1),
(5, 'Winter Runners', 0, 0, 0, 0, 1, 2),
(6, 'Snow Striders', 0, 0, 0, 0, 2, 2);

-- Insert Runners
INSERT INTO runners (id, name, gender) VALUES
(1, 'Mike Johnson', 'M'),
(2, 'Sarah Williams', 'F'),
(3, 'Tom Brown', 'M'),
(4, 'Lisa Davis', 'F'),
(5, 'James Wilson', 'M'),
(6, 'Emma Thompson', 'F'),
(7, 'David Miller', 'M'),
(8, 'Rachel Green', 'F'),
(9, 'Chris Taylor', 'M'),
(10, 'Amanda White', 'F');

-- Associate Runners with Teams
INSERT INTO team_runners (team_id, runner_id) VALUES
(1, 1), (1, 2),  -- Speed Demons runners
(2, 3), (2, 4),  -- Trail Blazers runners
(3, 5), (3, 6),  -- Road Warriors runners
(4, 7), (4, 8),  -- Hill Climbers runners
(5, 9), (5, 10); -- Winter Runners runners

-- Insert Races
INSERT INTO races (id, name, date, is_playoff, league_id) VALUES
(1, 'Spring Sprint', '2024-03-15', false, 1),
(2, 'Summer Chase', '2024-04-01', false, 1),
(3, 'Fall Dash', '2024-04-15', false, 1),
(4, 'Championship Run', '2024-05-01', true, 1),
(5, 'Winter Opener', '2024-11-01', false, 2);

-- Insert Race Results
INSERT INTO race_results (id, gender_place, overall_place, time, race_id, runner_id) VALUES
(1, 1, 1, '25:14', 1, 1), -- Mike Johnson results
(2, 1, 2, '25:24', 1, 2), -- Sarah Williams results
(3, 2, 3, '26:14', 1, 3), -- Tom Brown results
(4, 2, 4, '27:33', 1, 4), -- Lisa Davis results
(5, 1, 1, '21:18', 2, 5), -- James Wilson results
(6, 1, 2, '21:24', 2, 6), -- Emma Thompson results
(7, 2, 3, '22:32', 2, 7), -- David Miller results
(8, 2, 4, '24:02', 2, 8); -- Rachel Green results

-- Insert Matchups
INSERT INTO matchups (id, race_id, team1_id, team2_id, team1score, team2score) VALUES
(1, 1, 1, 2, 20, 25), -- Speed Demons vs Trail Blazers
(2, 1, 3, 4, 18, 28), -- Road Warriors vs Hill Climbers
(3, 2, 1, 3, 25, 20), -- Speed Demons vs Road Warriors
(4, 2, 2, 4, 27, 30); -- Trail Blazers vs Hill Climbers

-- Insert Drafts
INSERT INTO drafts (id, league_id, season, number_of_rounds, snake_order, start_time, is_complete, current_round, current_pick) VALUES
(1, 1, 2024, 6, true, '2024-03-01 10:00:00', true, 6, 4),  -- Completed Summer League draft
(2, 2, 2024, 6, true, '2024-10-15 14:00:00', false, 2, 1); -- In-progress Winter League draft

-- Insert Draft Order for Summer League Draft
INSERT INTO draft_order (draft_id, position, team_id) VALUES
(1, 0, 4), -- Hill Climbers first
(1, 1, 2), -- Trail Blazers second
(1, 2, 1), -- Speed Demons third
(1, 3, 3); -- Road Warriors fourth

-- Insert Draft Order for Winter League Draft
INSERT INTO draft_order (draft_id, position, team_id) VALUES
(2, 0, 6), -- Snow Striders first
(2, 1, 5); -- Winter Runners second

-- Insert Draft Picks for completed Summer League draft
INSERT INTO draft_picks (id, draft_id, team_id, runner_id, round, pick_number, pick_time) VALUES
-- Round 1
(1, 1, 4, 7, 1, 1, '2024-03-01 10:01:00'),  -- Hill Climbers pick David Miller
(2, 1, 2, 3, 1, 2, '2024-03-01 10:03:00'),  -- Trail Blazers pick Tom Brown
(3, 1, 1, 1, 1, 3, '2024-03-01 10:05:00'),  -- Speed Demons pick Mike Johnson
(4, 1, 3, 5, 1, 4, '2024-03-01 10:07:00'),  -- Road Warriors pick James Wilson
-- Round 2 (snake order reverses)
(5, 1, 3, 6, 2, 5, '2024-03-01 10:09:00'),  -- Road Warriors pick Emma Thompson
(6, 1, 1, 2, 2, 6, '2024-03-01 10:11:00'),  -- Speed Demons pick Sarah Williams
(7, 1, 2, 4, 2, 7, '2024-03-01 10:13:00'),  -- Trail Blazers pick Lisa Davis
(8, 1, 4, 8, 2, 8, '2024-03-01 10:15:00'),  -- Hill Climbers pick Rachel Green
-- Rounds 3-6 continue in same pattern...
(9, 1, 4, 9, 3, 9, '2024-03-01 10:17:00'),
(10, 1, 2, 10, 3, 10, '2024-03-01 10:19:00'),
(11, 1, 1, 1, 3, 11, '2024-03-01 10:21:00'),
(12, 1, 3, 2, 3, 12, '2024-03-01 10:23:00'),
(13, 1, 3, 3, 4, 13, '2024-03-01 10:25:00'),
(14, 1, 1, 4, 4, 14, '2024-03-01 10:27:00'),
(15, 1, 2, 5, 4, 15, '2024-03-01 10:29:00'),
(16, 1, 4, 6, 4, 16, '2024-03-01 10:31:00'),
(17, 1, 4, 7, 5, 17, '2024-03-01 10:33:00'),
(18, 1, 2, 8, 5, 18, '2024-03-01 10:35:00'),
(19, 1, 1, 9, 5, 19, '2024-03-01 10:37:00'),
(20, 1, 3, 10, 5, 20, '2024-03-01 10:39:00'),
(21, 1, 3, 1, 6, 21, '2024-03-01 10:41:00'),
(22, 1, 1, 2, 6, 22, '2024-03-01 10:43:00'),
(23, 1, 2, 3, 6, 23, '2024-03-01 10:45:00'),
(24, 1, 4, 4, 6, 24, '2024-03-01 10:47:00');

-- Insert Draft Picks for in-progress Winter League draft
INSERT INTO draft_picks (id, draft_id, team_id, runner_id, round, pick_number, pick_time) VALUES
-- Round 1
(25, 2, 6, 9, 1, 1, '2024-10-15 14:01:00'),  -- Snow Striders pick Chris Taylor
(26, 2, 5, 10, 1, 2, '2024-10-15 14:03:00'); -- Winter Runners pick Amanda White

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