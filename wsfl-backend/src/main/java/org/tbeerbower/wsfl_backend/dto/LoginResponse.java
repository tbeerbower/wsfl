package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.tbeerbower.wsfl_backend.model.User;
import java.util.Collections;

@Schema(description = "Response object containing authentication token and user details")
public class LoginResponse {
    @Schema(description = "JWT authentication token", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Details of the authenticated user")
    private UserDetailsDto user;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = new UserDetailsDto(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getPicture(),
            user.isActive(),
            user.getRoles(),
            Collections.emptyList() // Teams will be loaded separately if needed
        );
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDetailsDto getUser() {
        return user;
    }

    public void setUser(UserDetailsDto user) {
        this.user = user;
    }
}