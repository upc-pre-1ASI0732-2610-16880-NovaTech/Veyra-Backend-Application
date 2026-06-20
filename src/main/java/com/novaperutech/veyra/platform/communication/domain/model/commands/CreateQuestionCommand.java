package com.novaperutech.veyra.platform.communication.domain.model.commands;

public record CreateQuestionCommand(Long relativeId, Long residentId, Long nursingHomeId, String body) {
    public CreateQuestionCommand {
        if (relativeId == null) throw new IllegalArgumentException("relativeId is required");
        if (residentId == null) throw new IllegalArgumentException("residentId is required");
        if (nursingHomeId == null) throw new IllegalArgumentException("nursingHomeId is required");
        if (body == null || body.isBlank()) throw new IllegalArgumentException("body is required");
    }
}
