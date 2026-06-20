package com.novaperutech.veyra.platform.iam.domain.services;

import com.novaperutech.veyra.platform.iam.domain.model.aggregates.User;
import com.novaperutech.veyra.platform.iam.domain.model.commands.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;

public interface UserCommandService {
    Optional<ImmutablePair<User, String>> handle(SignInCommand command);
    Optional<User> handle(SignUpCommand command);

    /** Returns the TOTP secret generated for the user (not yet active until enableMfa). */
    String handle(SetupMfaCommand command);

    /** Verifies the first TOTP code and activates MFA on the account. */
    Optional<User> handle(EnableMfaCommand command);

    /** Disables MFA for the given user. */
    Optional<User> handle(DisableMfaCommand command);

    /** Verifies TOTP during sign-in and returns the JWT token. */
    Optional<String> handle(VerifyMfaCommand command);
}
