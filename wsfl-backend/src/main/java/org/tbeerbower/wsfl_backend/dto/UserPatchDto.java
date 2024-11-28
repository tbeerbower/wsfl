package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO for updating user properties")
public class UserPatchDto {
    @Schema(description = "Full name of the user", 
            example = "John Doe", 
            minLength = 2, 
            maxLength = 100,
            nullable = true)
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Schema(description = "Email address of the user", 
            example = "john.doe@example.com", 
            format = "email",
            nullable = true)
    @Email(message = "Email must be valid")
    private String email;

    @Schema(description = "User's password", 
            example = "********", 
            minLength = 6,
            format = "password",
            nullable = true)
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Schema(description = "User's picture src",
            example = "https://pics.org/smiley.png",
            nullable = true)
    private String picture;

    @Schema(description = "Is the user active", example = "true")
    private Boolean active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
