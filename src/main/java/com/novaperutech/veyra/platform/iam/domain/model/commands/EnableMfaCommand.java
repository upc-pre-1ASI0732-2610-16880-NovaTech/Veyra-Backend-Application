package com.novaperutech.veyra.platform.iam.domain.model.commands;

public record EnableMfaCommand(String username, String totpCode) {
    public EnableMfaCommand {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username is required");
        if (totpCode == null || totpCode.isBlank()) throw new IllegalArgumentException("totpCode is required");
    }
}
