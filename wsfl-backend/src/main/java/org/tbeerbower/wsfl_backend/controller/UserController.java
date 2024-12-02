package org.tbeerbower.wsfl_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.assembler.DraftDtoAssembler;
import org.tbeerbower.wsfl_backend.assembler.LeagueDtoAssembler;
import org.tbeerbower.wsfl_backend.assembler.MatchupDtoAssembler;
import org.tbeerbower.wsfl_backend.assembler.TeamDtoAssembler;
import org.tbeerbower.wsfl_backend.assembler.UserDtoAssembler;
import org.tbeerbower.wsfl_backend.dto.*;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.exception.ValidationException;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.User;
import org.tbeerbower.wsfl_backend.service.DraftService;
import org.tbeerbower.wsfl_backend.service.LeagueService;
import org.tbeerbower.wsfl_backend.service.MatchupService;
import org.tbeerbower.wsfl_backend.service.TeamService;
import org.tbeerbower.wsfl_backend.service.UserService;

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management APIs")
@CrossOrigin(origins = "http://localhost:8080")
public class UserController  {

    private final UserService userService;
    private final MatchupService matchupService;
    private final DraftService draftService;
    private final TeamService teamService;
    private final LeagueService leagueService;
    private final PasswordEncoder passwordEncoder;

    private final DraftDtoAssembler draftDtoAssembler;
    private final MatchupDtoAssembler matchupDtoAssembler;
    private final TeamDtoAssembler teamDtoAssembler;
    private final LeagueDtoAssembler leagueDtoAssembler;
    private final UserDtoAssembler userDtoAssembler;

    public UserController(UserService userService, MatchupService matchupService, DraftService draftService, TeamService teamService, LeagueService leagueService, PasswordEncoder passwordEncoder, DraftDtoAssembler draftDtoAssembler, MatchupDtoAssembler matchupDtoAssembler, TeamDtoAssembler teamDtoAssembler, LeagueDtoAssembler leagueDtoAssembler, UserDtoAssembler userDtoAssembler) {
        this.userService = userService;
        this.matchupService = matchupService;
        this.draftService = draftService;
        this.teamService = teamService;
        this.leagueService = leagueService;
        this.passwordEncoder = passwordEncoder;
        this.draftDtoAssembler = draftDtoAssembler;
        this.matchupDtoAssembler = matchupDtoAssembler;
        this.teamDtoAssembler = teamDtoAssembler;
        this.leagueDtoAssembler = leagueDtoAssembler;
        this.userDtoAssembler = userDtoAssembler;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all users",
        description = "Retrieves a paginated list of all users in the system"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved users"
    )
    @GetMapping
    public ResponseEntity<Page<UserDetailsDto>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        Page<UserDetailsDto> userDtos = users.map(userDtoAssembler::toDetailedModel);
        
        return ResponseEntity.ok(userDtos);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Get a user by ID",
        description = "Retrieves a specific user by their ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User found"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(type = "string", example = "User not found with id: 1"))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDto> getUserById(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        UserDetailsDto userDto = userDtoAssembler.toDetailedModel(user);

        return ResponseEntity.ok(userDto);
    }

    @Operation(
        summary = "Get teams owned by user",
        description = "Retrieves all teams owned by a specific user"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved teams"
    )
    @GetMapping("/{id}/teams")
    public ResponseEntity<Page<TeamDetailsDto>> getUserTeams(@PathVariable Long id,
                                                             @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        Page<TeamDetailsDto> teams = teamService.findByOwner(user, pageable)
                .map(teamDtoAssembler::toDetailedModel);
        
        return ResponseEntity.ok(teams);
    }

    @Operation(
            summary = "Get leagues administered by user",
            description = "Retrieves all leagues administered by a specific user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved leagues"
    )
    @GetMapping("/{id}/leagues")
    public ResponseEntity<Page<LeagueDetailsDto>> getUserLeagues(@PathVariable Long id,
                                                             @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        Page<LeagueDetailsDto> teams = leagueService.findByAdmin(user, pageable)
                .map(leagueDtoAssembler::toDetailsDto);

        return ResponseEntity.ok(teams);
    }

    @Operation(
            summary = "Get matchups for user teams",
            description = "Retrieves all matchups for a specific user's teams"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved matchups"
    )
    @GetMapping("/{id}/matchups")
    public ResponseEntity<Page<MatchupSummaryDto>> getUserMatchups(@PathVariable Long id,
                                                                 @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        Set<Long> teamIds = user.getTeams().stream().map(Team::getId).collect(Collectors.toSet());

        Page<MatchupSummaryDto> teams = matchupService.findByTeamIn(teamIds, teamIds, pageable)
                .map(matchupDtoAssembler::toModel);

        return ResponseEntity.ok(teams);
    }

    @Operation(
            summary = "Get drafts for user teams",
            description = "Retrieves all drafts for a specific user's teams"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved drafts"
    )
    @GetMapping("/{id}/drafts")
    public ResponseEntity<Page<DraftSummaryDto>> getUserDrafts(
            @PathVariable Long id,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        Page<DraftSummaryDto> teams = draftService.findByTeams(user.getTeams(), pageable)
                .map(draftDtoAssembler::toModel);

        return ResponseEntity.ok(teams);
    }

    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with the provided information"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Email already exists",
            content = @Content(schema = @Schema(type = "string", example = "Email already exists"))
        )
    })
    @PostMapping
    public ResponseEntity<UserDetailsDto> createUser(
            @Valid @RequestBody UserCreateDto createDto) {
        if (userService.existsByEmail(createDto.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        
        User user = convertToUser(createDto);
        user.setPassword(passwordEncoder.encode(createDto.getPassword()));
        User savedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDtoAssembler.toDetailedModel(savedUser));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update a user",
        description = "Updates an existing user's information"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(type = "string", example = "User not found with id: 1"))
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDetailsDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserCreateDto updateDto) {
        
        userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        if (userService.existsByEmailAndIdNot(updateDto.getEmail(), id)) {
            throw new ValidationException("Email already exists");
        }
        
        User user = convertToUser(updateDto);
        user.setId(id);
        user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        User updatedUser = userService.save(user);
        return ResponseEntity.ok(userDtoAssembler.toDetailedModel(updatedUser));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Partially update a user",
        description = "Updates specific fields of an existing user"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(type = "string", example = "User not found with id: 1"))
        )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UserDetailsDto> patchUser(
            @PathVariable Long id,
            @Valid @RequestBody UserPatchDto patchDto) {
        
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        if (patchDto.getEmail() != null && !patchDto.getEmail().equals(user.getEmail())) {
            if (userService.existsByEmail(patchDto.getEmail())) {
                throw new ValidationException("Email already exists");
            }
            user.setEmail(patchDto.getEmail());
        }
        
        if (patchDto.getName() != null) {
            user.setName(patchDto.getName());
        }
        
        if (patchDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(patchDto.getPassword()));
        }
        if (patchDto.getPicture() != null) {
            user.setPicture(patchDto.getPicture());
        }
        if (patchDto.isActive() != null) {
            user.setActive(patchDto.isActive());
        }
        User updatedUser = userService.save(user);
        return ResponseEntity.ok(userDtoAssembler.toDetailedModel(updatedUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete a user",
        description = "Deletes a user by their ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "User deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.findById(id).isPresent()) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Helper methods for DTO conversion
    private User convertToUser(UserCreateDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }
}