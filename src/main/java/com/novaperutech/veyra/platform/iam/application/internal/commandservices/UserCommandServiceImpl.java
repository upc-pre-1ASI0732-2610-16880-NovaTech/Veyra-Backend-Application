package com.novaperutech.veyra.platform.iam.application.internal.commandservices;

import com.novaperutech.veyra.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.novaperutech.veyra.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.novaperutech.veyra.platform.iam.domain.model.aggregates.User;
import com.novaperutech.veyra.platform.iam.domain.model.commands.*;
import com.novaperutech.veyra.platform.iam.domain.model.valueobjects.MfaMethod;
import com.novaperutech.veyra.platform.iam.domain.services.UserCommandService;
import com.novaperutech.veyra.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.novaperutech.veyra.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.novaperutech.veyra.platform.iam.infrastructure.sms.SmsService;
import com.novaperutech.veyra.platform.iam.infrastructure.totp.TotpService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private static final int SMS_CODE_TTL_MINUTES = 5;

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final TotpService totpService;
    private final SmsService smsService;

    public UserCommandServiceImpl(UserRepository userRepository,
                                  HashingService hashingService,
                                  TokenService tokenService,
                                  RoleRepository roleRepository,
                                  TotpService totpService,
                                  SmsService smsService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.totpService = totpService;
        this.smsService = smsService;
    }

    private String generateSmsCode() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var user = userRepository.findByUsername(command.username());
        if (user.isEmpty()) throw new RuntimeException("User not found");
        if (!hashingService.matches(command.password(), user.get().getPassword()))
            throw new RuntimeException("Invalid password");
        // If MFA is enabled, token is not issued yet — caller checks user.isMfaEnabled()
        if (user.get().isMfaEnabled()) {
            if (user.get().getMfaMethod() == MfaMethod.SMS) {
                var code = generateSmsCode();
                var expiresAt = LocalDateTime.now().plusMinutes(SMS_CODE_TTL_MINUTES);
                user.get().refreshSmsCode(code, expiresAt);
                userRepository.save(user.get());
                smsService.sendMfaCode(user.get().getPhoneNumber(), code);
            }
            return Optional.of(ImmutablePair.of(user.get(), ""));
        }
        var token = tokenService.generateToken(user.get().getUsername());
        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    @Override
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByUsername(command.username()))
            throw new RuntimeException("Username already exists");
        var roles = command.roles().stream()
                .map(role -> roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new RuntimeException("Role name not found")))
                .toList();
        var user = new User(command.username(), hashingService.encode(command.password()), roles);
        userRepository.save(user);
        return userRepository.findByUsername(command.username());
    }

    @Override
    public String handle(SetupMfaCommand command) {
        var user = userRepository.findByUsername(command.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Generate a new secret and store it temporarily (not active until EnableMfa)
        String secret = totpService.generateSecret();
        user.enableMfa(secret);
        userRepository.save(user);
        return secret;
    }

    @Override
    public Optional<User> handle(SetupSmsMfaCommand command) {
        var user = userRepository.findByUsername(command.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var code = generateSmsCode();
        var expiresAt = LocalDateTime.now().plusMinutes(SMS_CODE_TTL_MINUTES);
        user.beginSmsMfaSetup(command.phoneNumber(), code, expiresAt);
        var savedUser = userRepository.save(user);
        smsService.sendMfaCode(command.phoneNumber(), code);
        return Optional.of(savedUser);
    }

    @Override
    public Optional<User> handle(EnableMfaCommand command) {
        var user = userRepository.findByUsername(command.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getMfaMethod() == MfaMethod.SMS) {
            if (!user.isSmsCodeValid(command.totpCode()))
                throw new RuntimeException("Invalid or expired SMS code");
            user.activateSmsMfa();
            return Optional.of(userRepository.save(user));
        }

        if (user.getTotpSecret() == null) throw new RuntimeException("MFA setup not initiated. Call /mfa/setup first.");
        if (!totpService.verify(user.getTotpSecret(), command.totpCode()))
            throw new RuntimeException("Invalid TOTP code");
        user.enableMfa(user.getTotpSecret());
        return Optional.of(userRepository.save(user));
    }

    @Override
    public Optional<User> handle(DisableMfaCommand command) {
        var user = userRepository.findByUsername(command.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.disableMfa();
        return Optional.of(userRepository.save(user));
    }

    @Override
    public Optional<String> handle(VerifyMfaCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isMfaEnabled()) throw new RuntimeException("MFA is not enabled for this user");

        if (user.getMfaMethod() == MfaMethod.SMS) {
            if (!user.isSmsCodeValid(command.totpCode()))
                throw new RuntimeException("Invalid or expired SMS code");
        } else if (!totpService.verify(user.getTotpSecret(), command.totpCode())) {
            throw new RuntimeException("Invalid TOTP code");
        }
        return Optional.of(tokenService.generateToken(user.getUsername()));
    }
}
