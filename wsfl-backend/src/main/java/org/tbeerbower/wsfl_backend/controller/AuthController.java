package org.tbeerbower.wsfl_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.dto.LoginRequest;
import org.tbeerbower.wsfl_backend.dto.LoginResponse;
import org.tbeerbower.wsfl_backend.dto.TeamSummaryDto;
import org.tbeerbower.wsfl_backend.dto.UserDetailsDto;
import org.tbeerbower.wsfl_backend.dto.UserSummaryDto;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.exception.ValidationException;
import org.tbeerbower.wsfl_backend.model.User;
import org.tbeerbower.wsfl_backend.security.JwtUtil;
import org.tbeerbower.wsfl_backend.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Authentication", description = "Authentication management APIs")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8080")
public class AuthController  {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
        summary = "Login user",
        description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid credentials",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Invalid email or password"))
        )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        return userService.findByEmail(loginRequest.getEmail())
                .filter(user -> passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user);
                    return ResponseEntity.ok(new LoginResponse(token, user));
                })
                .orElseThrow(() -> new ValidationException("Invalid email or password"));
    }

    @Operation(
        summary = "Register new user",
        description = "Creates a new user account with the provided information"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Email already exists",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Email already exists"))
        )
    })
    @PostMapping("/register")
    public ResponseEntity<UserSummaryDto> register(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody User user) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            throw new ValidationException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton("USER"));
        User savedUser = userService.save(user);

        UserSummaryDto userDto = new UserSummaryDto(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getName()
        );

        return ResponseEntity.ok(userDto);
    }
}