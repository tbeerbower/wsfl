package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for Google authentication")
public class GoogleAuthRequest {
    @Schema(description = "Google auth code", example = "4/0AeanS0ZPhJ4ywnwGlf7hbe...")
    @NotBlank(message = "code is required")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}