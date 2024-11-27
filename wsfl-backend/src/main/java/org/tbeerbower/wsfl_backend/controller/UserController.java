package org.tbeerbower.wsfl_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.api.WsflResponse;
import org.tbeerbower.wsfl_backend.dto.*;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.exception.ValidationException;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.User;
import org.tbeerbower.wsfl_backend.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management APIs")
public class UserController extends BaseController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all users",
        description = "Retrieves a paginated list of all users in the system"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved users",
        content = @Content(mediaType = "application/json", 
                         schema = @Schema(implementation = UserSummaryDto.class))
    )
    @GetMapping
    public ResponseEntity<WsflResponse<List<UserSummaryDto>>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        List<UserSummaryDto> userDtos = users.map(this::convertToUserSummaryDto).getContent();
        
        WsflResponse<List<UserSummaryDto>> response = 
            new WsflResponse<>(userDtos);
        response.setMeta(new WsflResponse.Meta(
            users.getTotalElements(),
            users.getTotalPages(),
            users.getNumber(),
            users.getSize()
        ));
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Get a user by ID",
        description = "Retrieves a specific user by their ID"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = UserDetailsDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<WsflResponse<UserDetailsDto>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        UserDetailsDto userDto = convertToUserDetailsDto(user);
        List<Link> links = List.of(
            linkTo(methodOn(UserController.class).getUserTeams(id)).withRel("teams"),
            linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel()
        );
        
        return ok(userDto, links);
    }

    @Operation(
        summary = "Get teams owned by user",
        description = "Retrieves all teams owned by a specific user"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved teams",
        content = @Content(mediaType = "application/json", 
                         schema = @Schema(implementation = TeamSummaryDto.class))
    )
    @GetMapping("/{id}/teams")
    public ResponseEntity<WsflResponse<List<TeamSummaryDto>>> getUserTeams(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        List<TeamSummaryDto> teams = user.getTeams().stream()
                .map(this::convertToTeamSummaryDto)
                .collect(Collectors.toList());
        
        return ok(teams);
    }

    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with the provided information"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = UserDetailsDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Email already exists",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<WsflResponse<UserDetailsDto>> createUser(
            @Valid @RequestBody UserCreateDto createDto) {
        if (userService.existsByEmail(createDto.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        
        User user = convertToUser(createDto);
        user.setPassword(passwordEncoder.encode(createDto.getPassword()));
        User savedUser = userService.save(user);
        return created(convertToUserDetailsDto(savedUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update a user",
        description = "Updates an existing user's information"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = UserDetailsDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<WsflResponse<UserDetailsDto>> updateUser(
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
        return ok(convertToUserDetailsDto(updatedUser));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Partially update a user",
        description = "Updates specific fields of an existing user"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = UserDetailsDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<WsflResponse<UserDetailsDto>> patchUser(
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
        return ok(convertToUserDetailsDto(updatedUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete a user",
        description = "Deletes a user by their ID"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "User deleted successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<WsflResponse<Void>> deleteUser(@PathVariable Long id) {
        if (!userService.findById(id).isPresent()) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userService.deleteById(id);
        return noContent();
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