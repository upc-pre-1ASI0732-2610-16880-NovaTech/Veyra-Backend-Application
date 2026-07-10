package com.novaperutech.veyra.platform.iam.domain.model.commands;

public record SetupSmsMfaCommand(String username, String phoneNumber) {
    public SetupSmsMfaCommand {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username is required");
        if (phoneNumber == null || phoneNumber.isBlank()) throw new IllegalArgumentException("phoneNumber is required");
    }
}
