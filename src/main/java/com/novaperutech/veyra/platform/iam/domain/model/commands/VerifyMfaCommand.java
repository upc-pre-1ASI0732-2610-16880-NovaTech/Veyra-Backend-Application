package com.novaperutech.veyra.platform.iam.domain.model.commands;

public record VerifyMfaCommand(Long userId, String totpCode) {
    public VerifyMfaCommand {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (totpCode == null || totpCode.isBlank()) throw new IllegalArgumentException("totpCode is required");
    }
}
