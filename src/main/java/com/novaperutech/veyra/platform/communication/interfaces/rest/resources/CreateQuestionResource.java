package com.novaperutech.veyra.platform.communication.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateQuestionResource(
        @NotNull(message = "residentId is required") Long residentId,
        @NotNull(message = "nursingHomeId is required") Long nursingHomeId,
        @NotBlank(message = "body is required") String body
) {}
