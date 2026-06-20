package com.novaperutech.veyra.platform.iam.interfaces.rest.resources;

import java.util.List;

/**
 * Resource representing an authenticated user.
 * When mfaRequired is true, token is empty — the client must call POST /api/v1/authentication/mfa/verify
 * with the userId and a valid TOTP code to receive the actual JWT.
 */
public record AuthenticatedUserResource(Long id, String username, List<String> roles, String token, boolean mfaRequired) {}
