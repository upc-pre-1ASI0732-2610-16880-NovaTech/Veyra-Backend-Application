package com.novaperutech.veyra.platform.communication.interfaces.rest.resources;

import java.time.LocalDateTime;

public record QuestionResource(
        Long id,
        Long relativeId,
        Long residentId,
        Long nursingHomeId,
        String body,
        String answer,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
