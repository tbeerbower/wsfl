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

-- Insert Leagues
INSERT INTO leagues (id, name, max_teams, admin_id) VALUES
(1, 'WSFL', 6, 1);

INSERT INTO seasons (id, name) VALUES
(1, 'Winter 2024-2025');

INSERT INTO seasons (id, name) VALUES
(2, 'Spring 2025');

-- Insert Teams
INSERT INTO teams (id, name, owner_id, league_id) VALUES
(1, 'Speed Demons', 1, 1),
(2, 'Trail Blazers', 2, 1),
(3, 'Road Warriors', 3, 1),
(4, 'Hill Climbers', 4, 1),
(5, 'Winter Runners', 5, 1),
--(6, 'Snow Striders', 6, 1);
(6, 'Snow Striders', 1, 1);

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
(10, 'Amanda White', 'F'),
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
INSERT INTO races (id, name, date, is_playoff, is_canceled, season_id) VALUES
(1, 'Covered Bridge', '2024-12-15', false, false, 1),
(2, 'Jingle Bell', '2024-12-22', false, false, 1),
(3, 'Cham-Pain', '2025-01-01', false, false, 1),
(4, 'Tyler Challenge', '2025-01-05', false, false, 1),
(5, 'Wild Card', '2025-01-12', false, false, 1),
(6, 'Polar Bear', '2025-01-19', false, false, 1),
(7, 'Honest Abe', '2025-01-26', false, false, 1),
(8, 'Terrible Tyler', '2025-02-02', false, false, 1),
(9, 'Pick Your Way', '2025-02-09', true, false, 1),
(10, 'Eenie-Meanie-Minie-Moe', '2025-02-16', true, false, 1),

(11, 'Spring Fling', '2025-04-15', false, false, 2),
(12, 'Spring Break', '2025-05-16', false, false, 2),
(13, 'Bloomin Run', '2025-06-17', false, false, 2),
(14, 'Forth 5K', '2025-07-04', true, false, 2);

-- Insert Race Results for New Runners
-- We'll insert results for 2-9 races per runner to simulate participation
WITH race_choices AS (
    SELECT id FROM races ORDER BY id DESC
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
) results
JOIN runners r ON r.id = results.runner_id
JOIN races ra ON ra.id = results.race_id
WHERE
    results.participation_rank <= 9; -- Each runner participates in < 9 races

-- Insert Drafts
INSERT INTO drafts (id, league_id, season_id, name, number_of_rounds, snake_order, start_time) VALUES
(1, 1, 1, 'Winter 2024 Draft', 6, true, '2024-03-01 10:00:00');

INSERT INTO drafts (id, league_id, season_id, name, number_of_rounds, snake_order, start_time) VALUES
(2, 1, 2, 'Spring 2025 Draft', 6, true, '2025-03-01 10:00:00');


-- Insert Draft Order
INSERT INTO draft_order (draft_id, position, team_id) VALUES
(1, 0, 4), -- Hill Climbers first
(1, 1, 2), -- Trail Blazers second
(1, 2, 1), -- Speed Demons third
(1, 3, 6), -- Snow Striders fourth
(1, 4, 5), -- Winter Runners fifth
(1, 5, 3); -- Road Warriors sixth

INSERT INTO draft_order (draft_id, position, team_id) VALUES
(2, 0, 5), -- Winter Runners first
(2, 1, 6), -- Snow Striders second
(2, 2, 4), -- Hill Climbers third
(2, 3, 1), -- Speed Demons fourth
(2, 4, 2), -- Trail Blazers fifth
(2, 5, 3); -- Road Warriors sixth

-- Insert Draft Picks for the completed draft
INSERT INTO draft_picks (id, draft_id, team_id, runner_id, round, pick_number, pick_time) VALUES
-- Round 1
(1, 1, 4, 7, 1, 1, '2024-03-01 10:01:00'),  -- Hill Climbers pick David Miller
(2, 1, 2, 12, 1, 2, '2024-03-01 10:02:00'), -- Trail Blazers pick Brian Thompson
(3, 1, 1, 3, 1, 3, '2024-03-01 10:03:00'),  -- Speed Demons pick Tom Brown
(4, 1, 6, 25, 1, 4, '2024-03-01 10:04:00'), -- Snow Striders pick Oscar Smith
(5, 1, 5, 50, 1, 5, '2024-03-01 10:05:00'), -- Winter Runners pick Emily Garcia
(6, 1, 3, 18, 1, 6, '2024-03-01 10:06:00'), -- Road Warriors pick Henry Chen

-- Round 2
(7, 1, 3, 33, 2, 7, '2024-03-01 10:07:00'), -- Road Warriors pick Xavier Rodriguez
(8, 1, 5, 45, 2, 8, '2024-03-01 10:08:00'), -- Winter Runners pick Ava Johnson
(9, 1, 6, 29, 2, 9, '2024-03-01 10:09:00'), -- Snow Striders pick Samuel Anderson
(10, 1, 1, 39, 2, 10, '2024-03-01 10:10:00'), -- Speed Demons pick Isaac Turner
(11, 1, 2, 8, 2, 11, '2024-03-01 10:11:00'), -- Trail Blazers pick Rachel Green
(12, 1, 4, 16, 2, 12, '2024-03-01 10:12:00'), -- Hill Climbers pick Frank Williams

-- Round 3
(13, 1, 4, 43, 3, 13, '2024-03-01 10:13:00'), -- Hill Climbers pick Isabella Martinez
(14, 1, 2, 26, 3, 14, '2024-03-01 10:14:00'), -- Trail Blazers pick Patrick Taylor
(15, 1, 1, 35, 3, 15, '2024-03-01 10:15:00'), -- Speed Demons pick Zachary Phillips
(16, 1, 6, 41, 3, 16, '2024-03-01 10:16:00'), -- Snow Striders pick Olivia Parker
(17, 1, 5, 53, 3, 17, '2024-03-01 10:17:00'), -- Winter Runners pick Madison Wilson
(18, 1, 3, 22, 3, 18, '2024-03-01 10:18:00'), -- Road Warriors pick Liam Davis

-- Round 4
(19, 1, 3, 6, 4, 19, '2024-03-01 10:19:00'), -- Road Warriors pick Emma Thompson
(20, 1, 5, 55, 4, 20, '2024-03-01 10:20:00'), -- Winter Runners pick Ella Harris
(21, 1, 6, 19, 4, 21, '2024-03-01 10:21:00'), -- Snow Striders pick Ian Murphy
(22, 1, 1, 9, 4, 22, '2024-03-01 10:22:00'), -- Speed Demons pick Chris Taylor
(23, 1, 2, 47, 4, 23, '2024-03-01 10:23:00'), -- Trail Blazers pick Charlotte Davis
(24, 1, 4, 36, 4, 24, '2024-03-01 10:24:00'), -- Hill Climbers pick Ethan Carter

-- Round 5
(25, 1, 4, 11, 5, 25, '2024-03-01 10:25:00'), -- Hill Climbers pick Alex Rodriguez
(26, 1, 2, 5, 5, 26, '2024-03-01 10:26:00'),  -- Trail Blazers pick James Wilson
(27, 1, 1, 44, 5, 27, '2024-03-01 10:27:00'), -- Speed Demons pick Emma Rodriguez
(28, 1, 6, 21, 5, 28, '2024-03-01 10:28:00'), -- Snow Striders pick Kevin Brown
(29, 1, 5, 60, 5, 29, '2024-03-01 10:29:00'), -- Winter Runners pick Riley Scott
(30, 1, 3, 27, 5, 30, '2024-03-01 10:30:00'), -- Road Warriors pick Quentin Harris

-- Round 6
(31, 1, 3, 56, 6, 31, '2024-03-01 10:31:00'), -- Road Warriors pick Scarlett Martin
(32, 1, 5, 17, 6, 32, '2024-03-01 10:32:00'), -- Winter Runners pick George Lee
(33, 1, 6, 34, 6, 33, '2024-03-01 10:33:00'), -- Snow Striders pick Yannick Lee
(34, 1, 1, 14, 6, 34, '2024-03-01 10:34:00'), -- Speed Demons pick Daniel Kim
(35, 1, 2, 24, 6, 35, '2024-03-01 10:35:00'), -- Trail Blazers pick Nathan Wright
(36, 1, 4, 42, 6, 36, '2024-03-01 10:36:00'); -- Hill Climbers pick Sophia Chen


-- Insert Matchups for all 10 races
INSERT INTO matchups (id, race_id, team1_id, team2_id, draft_id) VALUES
-- Race 1 (already provided)
(1, 1, 1, 2, 1), -- Speed Demons vs Trail Blazers
(2, 1, 3, 4, 1), -- Road Warriors vs Hill Climbers
(3, 1, 5, 6, 1), -- Winter Runners vs Snow Striders

-- Race 2
(4, 2, 1, 3, 1), -- Speed Demons vs Road Warriors
(5, 2, 2, 5, 1), -- Trail Blazers vs Winter Runners
(6, 2, 4, 6, 1), -- Hill Climbers vs Snow Striders

-- Race 3
(7, 3, 1, 4, 1), -- Speed Demons vs Hill Climbers
(8, 3, 2, 6, 1), -- Trail Blazers vs Snow Striders
(9, 3, 3, 5, 1), -- Road Warriors vs Winter Runners

-- Race 4
(10, 4, 1, 5, 1), -- Speed Demons vs Winter Runners
(11, 4, 2, 4, 1), -- Trail Blazers vs Hill Climbers
(12, 4, 3, 6, 1), -- Road Warriors vs Snow Striders

-- Race 5
(13, 5, 1, 6, 1), -- Speed Demons vs Snow Striders
(14, 5, 2, 3, 1), -- Trail Blazers vs Road Warriors
(15, 5, 4, 5, 1), -- Hill Climbers vs Winter Runners

-- Race 6
(16, 6, 1, 2, 1), -- Speed Demons vs Trail Blazers
(17, 6, 3, 4, 1), -- Road Warriors vs Hill Climbers
(18, 6, 5, 6, 1), -- Winter Runners vs Snow Striders

-- Race 7
(19, 7, 1, 3, 1), -- Speed Demons vs Road Warriors
(20, 7, 2, 5, 1), -- Trail Blazers vs Winter Runners
(21, 7, 4, 6, 1), -- Hill Climbers vs Snow Striders

-- Race 8
(22, 8, 1, 4, 1), -- Speed Demons vs Hill Climbers
(23, 8, 2, 6, 1), -- Trail Blazers vs Snow Striders
(24, 8, 3, 5, 1), -- Road Warriors vs Winter Runners

-- Race 9
(25, 9, 1, 5, 1), -- Speed Demons vs Winter Runners
(26, 9, 2, 4, 1), -- Trail Blazers vs Hill Climbers
(27, 9, 3, 6, 1), -- Road Warriors vs Snow Striders

-- Race 10
(28, 10, 1, 6, 1), -- Speed Demons vs Snow Striders
(29, 10, 2, 3, 1), -- Trail Blazers vs Road Warriors
(30, 10, 4, 5, 1); -- Hill Climbers vs Winter Runners

-- Insert Matchups for all 4 spring races
INSERT INTO matchups (id, race_id, team1_id, team2_id, draft_id) VALUES
-- Race 1 (already provided)
(31, 11, 1, 2, 2), -- Speed Demons vs Trail Blazers
(32, 11, 3, 4, 2), -- Road Warriors vs Hill Climbers
(33, 11, 5, 6, 2), -- Winter Runners vs Snow Striders

-- Race 2
(34, 12, 1, 3, 2), -- Speed Demons vs Road Warriors
(35, 12, 2, 5, 2), -- Trail Blazers vs Winter Runners
(36, 12, 4, 6, 2), -- Hill Climbers vs Snow Striders

-- Race 3
(37, 13, 1, 4, 2), -- Speed Demons vs Hill Climbers
(38, 13, 2, 6, 2), -- Trail Blazers vs Snow Striders
(39, 13, 3, 5, 2), -- Road Warriors vs Winter Runners

-- Race 4
(40, 14, 1, 5, 2), -- Speed Demons vs Winter Runners
(41, 14, 2, 4, 2), -- Trail Blazers vs Hill Climbers
(42, 14, 3, 6, 2); -- Road Warriors vs Snow Striders


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

