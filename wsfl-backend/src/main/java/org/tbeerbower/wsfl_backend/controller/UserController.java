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
import org.tbeerbower.wsfl_backend.dto.*;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.exception.ValidationException;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.User;
import org.tbeerbower.wsfl_backend.service.TeamService;
import org.tbeerbower.wsfl_backend.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management APIs")
public class UserController  {

    private final UserService userService;
    private final TeamService teamService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, TeamService teamService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.teamService = teamService;
        this.passwordEncoder = passwordEncoder;
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
    public ResponseEntity<Page<UserSummaryDto>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        Page<UserSummaryDto> userDtos = users.map(this::convertToUserSummaryDto);
        
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
        
        UserDetailsDto userDto = convertToUserDetailsDto(user);

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
    public ResponseEntity<Page<TeamSummaryDto>> getUserTeams(@PathVariable Long id,
                                                             @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        Page<TeamSummaryDto> teams = teamService.findByOwner(user, pageable)
                .map(this::convertToTeamSummaryDto);
        
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
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToUserDetailsDto(savedUser));

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
        return ResponseEntity.ok(convertToUserDetailsDto(updatedUser));
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
        
        User updatedUser = userService.save(user);
        return ResponseEntity.ok(convertToUserDetailsDto(updatedUser));
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
    private UserDetailsDto convertToUserDetailsDto(User user) {
        List<TeamSummaryDto> teamSummaries = user.getTeams().stream()
                .map(this::convertToTeamSummaryDto)
                .collect(Collectors.toList());

        return new UserDetailsDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRoles(),
            teamSummaries
        );
    }

    private UserSummaryDto convertToUserSummaryDto(User user) {
        return new UserSummaryDto(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }

    private TeamSummaryDto convertToTeamSummaryDto(Team team) {
        return new TeamSummaryDto(
            team.getId(),
            team.getName(),
            team.getWins(),
            team.getLosses(),
            team.getTies(),
            team.getTotalScore()
        );
    }

    private User convertToUser(UserCreateDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }
}