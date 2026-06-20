package com.novaperutech.veyra.platform.iam.domain.model.commands;

public record DisableMfaCommand(String username) {
    public DisableMfaCommand {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username is required");
    }
}
