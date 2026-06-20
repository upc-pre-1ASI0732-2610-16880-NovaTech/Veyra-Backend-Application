package com.novaperutech.veyra.platform.iam.interfaces.rest.resources;

public record MfaSetupResponseResource(String secret, String otpAuthUrl) {}
