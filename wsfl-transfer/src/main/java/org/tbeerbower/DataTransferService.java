package org.tbeerbower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class DataTransferService {

    private final JdbcTemplate sourceJdbc;
    private final JdbcTemplate targetJdbc;

    @Autowired
    public DataTransferService(
            @Qualifier("sourceJdbcTemplate") JdbcTemplate sourceJdbc,
            @Qualifier("targetJdbcTemplate") JdbcTemplate targetJdbc) {
        this.sourceJdbc = sourceJdbc;
        this.targetJdbc = targetJdbc;
    }

    private Map<Integer, Long> leagueIdMapping = new HashMap<>();
    private Map<Integer, Long> seasonIdMapping = new HashMap<>();
    private Map<Integer, Long> draftIdMapping = new HashMap<>();
    private Map<Integer, Long> raceIdMapping = new HashMap<>();
    private Map<Integer, Long> runnerIdMapping = new HashMap<>();
    private Map<Integer, Long> teamIdMapping = new HashMap<>();
    private Map<Integer, Long> userIdMapping = new HashMap<>();

    private Map<Integer, String> seasonNameMapping = new HashMap<>();
    private Map<Integer, Integer> seasonRoundsMapping = new HashMap<>();
    private Map<Integer, Integer> seasonSupplementalRoundsMapping = new HashMap<>();


    @Transactional
    public void transferData() {

        try {
            transferUsers();
        } catch (DataAccessException e) {
            System.out.println("Skipping users table - not found in source database");
            // Use default mapping for leagues
            userIdMapping.put(1, 1L);
        }

        try {
            transferLeagues();
        } catch (DataAccessException e) {
            System.out.println("Skipping leagues table - not found in source database");
            // Use default mapping for leagues
            leagueIdMapping.put(1, 1L);
        }

        try {
            transferSeasons();
        } catch (DataAccessException e) {
            System.out.println("Skipping seasons table - not found in source database");
            // Use default mapping for leagues
            leagueIdMapping.put(1, 1L);
        }

        try {
            transferRunners();
        } catch (DataAccessException e) {
            System.out.println("Skipping runners table - not found in source database");
            // Use default mapping for runners
            runnerIdMapping.put(1, 1L);
        }

//        try {
//            transferRaceDefinitions();
//        } catch (DataAccessException e) {
//            System.out.println("Skipping race_definition table - not found in source database");
//        }

        try {
            transferRaces();
        } catch (DataAccessException e) {
            System.out.println("Skipping races table - not found in source database");
            // Use default mapping for races
            raceIdMapping.put(1, 1L);
        }

        try {
            transferTeams();
        } catch (DataAccessException e) {
            System.out.println("Skipping teams table - not found in source database");
            // Use default mapping for teams
            teamIdMapping.put(1, 1L);
        }

        try {
            transferResults();
        } catch (DataAccessException e) {
            System.out.println("Skipping results table - not found in source database");
        }

        try {
            transferDrafts();
        } catch (DataAccessException e) {
            System.out.println("Skipping seasons draft - not found in source database");
            // Use default mapping for leagues
            draftIdMapping.put(1, 1L);
        }

        try {
            transferMatchups();
        } catch (DataAccessException e) {
            System.out.println("Skipping matchups table - not found in source database");
        }
    }

    private boolean tableExists(JdbcTemplate jdbc, String tableName) {
        try {
            jdbc.queryForObject("SELECT 1 FROM " + tableName + " LIMIT 1", Integer.class);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    private void transferUsers() {
        try {
            List<Map<String, Object>> leagues = sourceJdbc.queryForList("SELECT * FROM usr");
            for (Map<String, Object> league : leagues) {
                try {
                    int sourceId = (Integer) league.get("id");
                    String name = (String) league.get("name");
                    Boolean active = (Boolean) league.get("active");
                    String email = (String) league.get("email");
                    String picture = (String) league.get("picture");
                    String roles = (String) league.get("roles");


                    targetJdbc.update(
                        "INSERT INTO users (name, active, email, picture, password) VALUES (?, ?, ?, ?, ?)",
                        name, active, email, picture, "$2a$10$TXkorQjx0GhjdjgJ2A84.OQ5W3Q5OWWu.SXXCKjyDt.vXD2WdzxyS"
                    );
                    
                    Long targetId = targetJdbc.queryForObject(
                        "SELECT id FROM users WHERE email = ?",
                        Long.class, 
                        email
                    );
                    userIdMapping.put(sourceId, targetId);
                    String[] rolesArray = roles.split(",");
                    for (String role : rolesArray) {
                        targetJdbc.update(
                            "INSERT INTO user_roles (user_id, role) VALUES (?, ?)",
                            targetId, role.trim()
                        );
                    }
                } catch (DataAccessException e) {
                    System.out.println("Failed to transfer user: " + league.get("name"));
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Skipping usr table - not found in source database");
            userIdMapping.put(1, 1L);
        }
    }

    private void transferLeagues() {
        try {
            List<Map<String, Object>> leagues = sourceJdbc.queryForList("SELECT * FROM league");
            for (Map<String, Object> league : leagues) {
                try {
                    int sourceId = (Integer) league.get("id");
                    String name = (String) league.get("name");

                    targetJdbc.update(
                            "INSERT INTO leagues (name, max_teams, admin_id) VALUES (?, ?, ?)",
                            name, 10, 1
                    );

                    Long targetId = targetJdbc.queryForObject(
                            "SELECT id FROM leagues WHERE name = ?",
                            Long.class,
                            name
                    );
                    leagueIdMapping.put(sourceId, targetId);

                } catch (DataAccessException e) {
                    System.out.println("Failed to transfer league: " + league.get("name"));
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Skipping league table - not found in source database");
            leagueIdMapping.put(1, 1L);
        }
    }

    private void transferSeasons() {
        try {
            List<Map<String, Object>> seasons = sourceJdbc.queryForList("SELECT * FROM season");
            for (Map<String, Object> season : seasons) {
                try {
                    int sourceId = (Integer) season.get("id");
                    String name = (String) season.get("name");

                    targetJdbc.update(
                            "INSERT INTO seasons (name) VALUES (?)",
                            name
                    );

                    Long targetId = targetJdbc.queryForObject(
                            "SELECT id FROM seasons WHERE name = ?",
                            Long.class,
                            name
                    );
                    seasonIdMapping.put(sourceId, targetId);
                    seasonNameMapping.put(sourceId, name);
                    seasonRoundsMapping.put(sourceId, (Integer) season.get("draft_rounds"));
                    seasonSupplementalRoundsMapping.put(sourceId, (Integer) season.get("supplemental_rounds"));
                } catch (DataAccessException e) {
                    System.out.println("Failed to transfer season: " + season.get("name"));
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Skipping season table - not found in source database");
            seasonIdMapping.put(1, 1L);
        }
    }

    private void transferDrafts() {
        try {
            List<Map<String, Object>> drafts = sourceJdbc.queryForList("SELECT league_id, season_id FROM team_season group by league_id, season_id");
            for (Map<String, Object> draft : drafts) {
                try {
                    int leagueId = (Integer) draft.get("league_id");
                    int seasonId = (Integer) draft.get("season_id");

                    String name = seasonNameMapping.get(seasonId) + " Draft";
                    targetJdbc.update(
                            "INSERT INTO drafts (name, season_id, league_id, number_of_rounds, snake_order) VALUES (?, ? , ?, ?, ?)",
                            name,
                            seasonIdMapping.get(seasonId),
                            leagueIdMapping.get(leagueId),
                            seasonRoundsMapping.get(seasonId), true
                    );

                    Long targetId = targetJdbc.queryForObject(
                            "SELECT id FROM drafts WHERE name = ?",
                            Long.class,
                            name
                    );
                    // Use season ID as key for draft ID mapping
                    draftIdMapping.put(seasonId, targetId);

                    List<Map<String, Object>> draftOrders = sourceJdbc.queryForList("SELECT * FROM team_season WHERE league_id = ? AND season_id = ? ORDER BY draft_order ASC", leagueId, seasonId);
                    for (Map<String, Object> draftOrder : draftOrders) {
                        int id = (Integer) draftOrder.get("id");
                        int teamId = (Integer) draftOrder.get("team_id");
                        int order = (Integer) draftOrder.get("draft_order");

                        targetJdbc.update(
                                "INSERT INTO draft_order (position, draft_id, team_id) VALUES (?, ? , ?)",
                                order - 1,
                                targetId,
                                teamIdMapping.get(teamId)
                        );
                        List<Map<String, Object>> draftPicks = sourceJdbc.queryForList("SELECT * FROM team_runner WHERE team_season_id = ? order by draft_order", id);
                        int round = 0;
                        for (Map<String, Object> draftPick : draftPicks) {
                            int runnerId = (Integer) draftPick.get("runner_id");
                            int teamsInDraft = draftOrders.size();
                            boolean isSupplemental = round >= seasonRoundsMapping.get(seasonId) - seasonSupplementalRoundsMapping.get(seasonId);
                            int pickNumber = round % 2 == 0 ? round * teamsInDraft + order : (round + 1) * teamsInDraft - order + 1;

                            String yearRange = seasonNameMapping.get(seasonId);
                            String firstYear = yearRange.split("-")[0];
                            int year = Integer.parseInt(firstYear);
                            LocalDateTime baseDateTime = LocalDateTime.of(year, 12, isSupplemental ? 16 : 10, 12, 0);
                            LocalDateTime pickTime = baseDateTime.plusMinutes(15L * (pickNumber - 1));

                            targetJdbc.update(
                                    "INSERT INTO draft_picks (pick_number, round, draft_id, runner_id, team_id, pick_time) VALUES (?, ?, ?, ?, ?, ?)",
                                    pickNumber,
                                    ++round,
                                    targetId,
                                    runnerIdMapping.get(runnerId),
                                    teamIdMapping.get(teamId),
                                    pickTime  // estimate pick time
                            );
                        }
                    }
                } catch (DataAccessException e) {
                    int seasonId = (Integer) draft.get("season_id");
                    String name = seasonNameMapping.get(seasonId) + " Draft";
                    System.out.println("Failed to transfer draft: " + name);
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Skipping draft table - not found in source database");
            seasonIdMapping.put(1, 1L);
        }
    }


    private void transferRunners() {
        try {
            List<Map<String, Object>> runners = sourceJdbc.queryForList("SELECT * FROM runner");  
            for (Map<String, Object> runner : runners) {
                try {
                    int sourceId = (Integer) runner.get("id");
                    String name = (String) runner.get("name");
                    String gender = (String) runner.get("gender");

                    targetJdbc.update(
                        "INSERT INTO runners (name, gender) VALUES (?, ?)",
                        name, gender
                    );
                    
                    Long targetId = targetJdbc.queryForObject(
                        "SELECT id FROM runners WHERE name = ?", 
                        Long.class, 
                        name
                    );
                    runnerIdMapping.put(sourceId, targetId);
                } catch (DataAccessException e) {
                    System.out.println("Failed to transfer runner: " + runner.get("name"));
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Skipping runner table - not found in source database");
            runnerIdMapping.put(1, 1L);
        }
    }

    private void transferRaceDefinitions() {
        try {
            List<Map<String, Object>> definitions = sourceJdbc.queryForList("SELECT * FROM race_definition");  
            for (Map<String, Object> def : definitions) {
                try {
                    String name = (String) def.get("name");
                    String shortName = (String) def.get("short_name");
                    
                    targetJdbc.update(
                        "INSERT INTO race_definitions (name, short_name) VALUES (?, ?)",  
                        name, shortName
                    );
                } catch (DataAccessException e) {
                    System.out.println("Failed to transfer race definition: " + def.get("name"));
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Skipping race_definition table - not found in source database");
        }
    }

    private void transferRaces() {
        try {
            List<Map<String, Object>> races = sourceJdbc.queryForList("SELECT * FROM race");  
            for (Map<String, Object> race : races) {
                try {
                    int sourceId = (Integer) race.get("id");
                    String name = (String) race.get("name");
                    boolean cancelled = (Boolean) race.get("cancelled");
                    int seasonId = (Integer) race.get("season_id");
                    int week = (Integer) race.get("week");
                    name += " " + seasonNameMapping.get(seasonId);

                    String yearRange = seasonNameMapping.get(seasonId);

                    // Extract the first year
                    String firstYear = yearRange.split("-")[0];
                    int year = Integer.parseInt(firstYear);
                    // Create a LocalDate instance for December 15th of the extracted year
                    LocalDate baseDate = LocalDate.of(year, 12, 15);

                    targetJdbc.update(
                        "INSERT INTO races (name, season_id, is_canceled, date) VALUES (?, ?, ?, ?)",
                        name, seasonIdMapping.get(seasonId), cancelled,
                            baseDate.plusWeeks(week - 1) // estimated date
                    );


                    Long targetId = targetJdbc.queryForObject(
                        "SELECT id FROM races WHERE name = ?", 
                        Long.class, 
                        name
                    );
                    raceIdMapping.put(sourceId, targetId);
                } catch (DataAccessException e) {
                    System.out.println("Failed to transfer race: " + race.get("name"));
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Skipping race table - not found in source database");
            raceIdMapping.put(1, 1L);
        }
    }

    private void transferTeams() {
        try {
            List<Map<String, Object>> teams = sourceJdbc.queryForList("SELECT * FROM team");  
            for (Map<String, Object> team : teams) {
                try {
                    int sourceId = (Integer) team.get("id");
                    String name = (String) team.get("name");
                    Integer ownerId = (Integer) team.get("owner_id");
                    
                    targetJdbc.update(
                        "INSERT INTO teams (name, owner_id) VALUES (?, ?)",
                        name, userIdMapping.get(ownerId)
                    );
                    
                    Long targetId = targetJdbc.queryForObject(
                        "SELECT id FROM teams WHERE name = ?", 
                        Long.class, 
                        name
                    );
                    teamIdMapping.put(sourceId, targetId);
                } catch (DataAccessException e) {
                    System.out.println("Failed to transfer team: " + team.get("name"));
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Skipping team table - not found in source database");
            teamIdMapping.put(1, 1L);
        }
    }

    private void transferResults() {
        try {
            List<Map<String, Object>> results = sourceJdbc.queryForList("SELECT * FROM result");  
            for (Map<String, Object> result : results) {
                try {
                    int runnerId = (Integer) result.get("runner_id");
                    int raceId = (Integer) result.get("race_id");
                    int placeGender = (Integer) result.get("place_gender");
                    int placeOverall = (Integer) result.get("place_overall");
                    String time = (String) result.get("time");
                    
                    Long newRunnerId = runnerIdMapping.getOrDefault(runnerId, 1L);
                    Long newRaceId = raceIdMapping.getOrDefault(raceId, 1L);
                    
                    targetJdbc.update(
                        "INSERT INTO race_results (runner_id, race_id, gender_place, overall_place, time) VALUES (?, ?, ?, ?, ?)",
                        newRunnerId, newRaceId, placeGender, placeOverall, time
                    );
                } catch (DataAccessException e) {
                    System.out.println("Failed to transfer result for runner_id: " + result.get("runner_id"));
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Skipping result table - not found in source database");
        }
    }

    private void transferMatchups() {
        try {
            List<Map<String, Object>> matchups = sourceJdbc.queryForList("SELECT * FROM matchup");  
            for (Map<String, Object> matchup : matchups) {
                try {
                    int raceId = (Integer) matchup.get("race_id");
                    int teamAId = (Integer) matchup.get("team_a_id");
                    int teamBId = (Integer) matchup.get("team_b_id");
                    int seasonId = (Integer) matchup.get("season_id");
                    
                    Long newRaceId = raceIdMapping.getOrDefault(raceId, 1L);
                    Long newTeamAId = teamIdMapping.getOrDefault(teamAId, 1L);
                    Long newTeamBId = teamIdMapping.getOrDefault(teamBId, 1L);
                    Long newDraftId = draftIdMapping.getOrDefault(seasonId, 1L);

                    targetJdbc.update(
                        "INSERT INTO matchups (race_id, draft_id, team1_id, team2_id, is_playoff, is_championship) VALUES (?, ?, ?, ?, ?, ?)",
                        newRaceId, newDraftId, newTeamAId, newTeamBId, false, false
                    );
                } catch (DataAccessException e) {
                    System.out.println("Failed to transfer matchup for race_id: " + matchup.get("race_id"));
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Skipping matchup table - not found in source database");
        }
    }
}
