-- Clear existing data
TRUNCATE TABLE users, leagues, teams, runners, races, race_results, matchups, team_runners, user_roles, drafts, draft_order, draft_picks CASCADE;

-- Insert Users with hashed passwords (password is 'password')
INSERT INTO users (id, email, name, picture, active, password) VALUES
(1, 'tbeerbower@gmail.com', 'Tom B', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS'),
(2, 'jane.smith@example.com', 'Jane Smith', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS'),
(3, 'bob.wilson@example.com', 'Bob Wilson', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS'),
(4, 'alice.jones@example.com', 'Alice Jones', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS'),
(5, 'john.doe@example.com', 'John Doe', '', true, '$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS');

-- Insert User Roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_USER'),
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_USER'),
(4, 'ROLE_USER'),
(5, 'ROLE_USER');

-- Insert Leagues
INSERT INTO leagues (id, name, max_teams, admin_id) VALUES
(1, 'Summer League', 4, 1),
(2, 'Winter League', 4, 1);

INSERT INTO seasons (id, name) VALUES
(1, 'Summer 2024'),
(2, 'Winter 2024-2025');

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

-- Insert 90 Additional Runners with Explicit IDs
INSERT INTO runners (id, name, gender) VALUES
-- Male Runners (IDs 11-40)
(11, 'Alex Rodriguez', 'M'),
(12, 'Brian Thompson', 'M'),
(13, 'Carlos Martinez', 'M'),
(14, 'Daniel Kim', 'M'),
(15, 'Eric Johnson', 'M'),
(16, 'Frank Williams', 'M'),
(17, 'George Lee', 'M'),
(18, 'Henry Chen', 'M'),
(19, 'Ian Murphy', 'M'),
(20, 'Jack Robinson', 'M'),
(21, 'Kevin Brown', 'M'),
(22, 'Liam Davis', 'M'),
(23, 'Michael Garcia', 'M'),
(24, 'Nathan Wright', 'M'),
(25, 'Oscar Smith', 'M'),
(26, 'Patrick Taylor', 'M'),
(27, 'Quentin Harris', 'M'),
(28, 'Ryan Martinez', 'M'),
(29, 'Samuel Anderson', 'M'),
(30, 'Tyler Jones', 'M'),
(31, 'Victor Perez', 'M'),
(32, 'William Clark', 'M'),
(33, 'Xavier Rodriguez', 'M'),
(34, 'Yannick Lee', 'M'),
(35, 'Zachary Phillips', 'M'),
(36, 'Ethan Carter', 'M'),
(37, 'Gabriel Scott', 'M'),
(38, 'Harrison Moore', 'M'),
(39, 'Isaac Turner', 'M'),
(40, 'Jason Mitchell', 'M'),
-- Female Runners (IDs 41-70)
(41, 'Olivia Parker', 'F'),
(42, 'Sophia Chen', 'F'),
(43, 'Isabella Martinez', 'F'),
(44, 'Emma Rodriguez', 'F'),
(45, 'Ava Johnson', 'F'),
(46, 'Mia Williams', 'F'),
(47, 'Charlotte Davis', 'F'),
(48, 'Abigail Lee', 'F'),
(49, 'Harper Thompson', 'F'),
(50, 'Emily Garcia', 'F'),
(51, 'Elizabeth Brown', 'F'),
(52, 'Sofia Anderson', 'F'),
(53, 'Madison Wilson', 'F'),
(54, 'Avery Taylor', 'F'),
(55, 'Ella Harris', 'F'),
(56, 'Scarlett Martin', 'F'),
(57, 'Grace Miller', 'F'),
(58, 'Chloe Jones', 'F'),
(59, 'Victoria Clark', 'F'),
(60, 'Riley Scott', 'F'),
(61, 'Lily Robinson', 'F'),
(62, 'Zoe Phillips', 'F'),
(63, 'Aubrey Nelson', 'F'),
(64, 'Hannah Wright', 'F'),
(65, 'Layla Murphy', 'F'),
(66, 'Samantha Turner', 'F'),
(67, 'Anna Mitchell', 'F'),
(68, 'Natalie White', 'F'),
(69, 'Lauren Kim', 'F'),
(70, 'Julia Carter', 'F');


-- Insert Races
INSERT INTO races (id, name, date, is_playoff, season_id) VALUES
(1, 'Spring Sprint', '2024-03-15', false, 1),
(2, 'Summer Chase', '2024-04-01', false, 1),
(3, 'Fall Dash', '2024-09-22', false, 1),
(4, 'Championship Run', '2024-10-01', true, 1),
(5, 'Winter Opener', '2024-11-01', false, 2);

-- Insert 5 Additional Races with Explicit IDs
INSERT INTO races (id, name, date, is_playoff, season_id) VALUES
(6, 'Twilight Trail', '2024-04-22', false, 1),
(7, 'Mountain Marathon', '2024-05-10', false, 1),
(8, 'Frosty Frontier', '2024-11-15', false, 2),
(9, 'Arctic Challenge', '2024-12-01', false, 2),
(10, 'Global Championship', '2024-12-15', true, 2);


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

-- Insert Race Results for New Runners
-- We'll insert results for 2-3 races per runner to simulate participation
WITH race_choices AS (
    SELECT id FROM races WHERE id > 4
)
INSERT INTO race_results (id, gender_place, overall_place, time, race_id, runner_id)
SELECT
    -- Generate sequential IDs for race_results
    (ROW_NUMBER() OVER () + (SELECT COALESCE(MAX(id), 0) FROM race_results)) AS id,
    CASE
        WHEN r.gender = 'M' THEN (ROW_NUMBER() OVER (PARTITION BY r.gender, ra.id ORDER BY result_time))
        ELSE (ROW_NUMBER() OVER (PARTITION BY r.gender, ra.id ORDER BY result_time))
    END as gender_place,
    (ROW_NUMBER() OVER (PARTITION BY ra.id ORDER BY result_time)) as overall_place,
    result_time,
    ra.id as race_id,
    r.id as runner_id
FROM (
    -- Generate multiple results for each runner
    SELECT
        runners.id AS runner_id,
        gender,
        race_choices.id AS race_id,
        (
            CASE
                WHEN gender = 'M' THEN
                    (INTERVAL '20 minutes' + (random() * INTERVAL '10 minutes'))::time
                ELSE
                    (INTERVAL '22 minutes' + (random() * INTERVAL '10 minutes'))::time
            END
        )::varchar as result_time,
        DENSE_RANK() OVER (PARTITION BY runners.id ORDER BY RANDOM()) as participation_rank
    FROM
        runners,
        race_choices
    WHERE
        runners.id > 10 -- Skip the original 10 runners
) results
JOIN runners r ON r.id = results.runner_id
JOIN races ra ON ra.id = results.race_id
WHERE
    results.participation_rank <= 3; -- Each runner participates in 2-3 races

-- Insert Drafts
INSERT INTO drafts (id, league_id, season_id, name, number_of_rounds, snake_order, start_time, is_started, is_complete, current_round, current_pick) VALUES
(1, 1, 1, 'Summer 2024 Draft', 6, true, '2024-03-01 10:00:00', true, true, 6, 4),  -- Completed Summer League draft
(2, 2, 2, 'Winter 2024 Draft', 6, true, '2024-10-15 14:00:00', true, false, 2, 1), -- In-progress Winter League draft
(3, 1, 1, 'Spring 2025 Draft', 8, false, '2024-10-15 14:00:00', false, false, 2, 1); -- Not started Spring League draft

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


-- Insert Matchups
INSERT INTO matchups (id, race_id, team1_id, team2_id, team1score, team2score, draft_id) VALUES
(1, 1, 1, 2, 20, 25, 1), -- Speed Demons vs Trail Blazers
(2, 1, 3, 4, 18, 28, 1), -- Road Warriors vs Hill Climbers
(3, 2, 1, 3, 25, 20, 1), -- Speed Demons vs Road Warriors
(4, 2, 2, 4, 27, 30, 1); -- Trail Blazers vs Hill Climbers

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

