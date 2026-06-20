package com.novaperutech.veyra.platform.iam.domain.model.commands;

public record SetupMfaCommand(String username) {
    public SetupMfaCommand {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username is required");
    }
}
