package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Set;

@Schema(description = "Detailed representation of a user")
public class UserDetailsDto {
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Schema(description = "Email address of the user", example = "user@example.com")
    private String email;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String name;

    @Schema(description = "Roles assigned to the user", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private Set<String> roles;

    @Schema(description = "Teams associated with the user")
    private List<TeamSummaryDto> teams;

    public UserDetailsDto(Long id, String email, String name, Set<String> roles, List<TeamSummaryDto> teams) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.teams = teams;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public Set<String> getRoles() { return roles; }
    public List<TeamSummaryDto> getTeams() { return teams; }
}