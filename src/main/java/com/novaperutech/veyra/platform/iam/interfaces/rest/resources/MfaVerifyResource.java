package com.novaperutech.veyra.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MfaVerifyResource(
        @NotNull(message = "userId is required") Long userId,
        @NotBlank(message = "TOTP code is required")
        @Size(min = 6, max = 6, message = "TOTP code must be exactly 6 digits")
        String code
) {}
