package com.novaperutech.veyra.platform.iam.interfaces.rest;

import com.novaperutech.veyra.platform.iam.domain.model.commands.*;
import com.novaperutech.veyra.platform.iam.domain.services.UserCommandService;
import com.novaperutech.veyra.platform.iam.infrastructure.totp.TotpService;
import com.novaperutech.veyra.platform.iam.interfaces.rest.resources.*;
import com.novaperutech.veyra.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/authentication/mfa", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Authentication")
public class MfaController {

    private final UserCommandService userCommandService;
    private final TotpService totpService;

    public MfaController(UserCommandService userCommandService, TotpService totpService) {
        this.userCommandService = userCommandService;
        this.totpService = totpService;
    }

    @PostMapping("/setup")
    @Operation(
            summary = "Initialize MFA setup",
            description = "Generates a TOTP secret and returns the otpauth:// URL to scan with an authenticator app (Google Authenticator, Authy). " +
                    "MFA is NOT active yet — confirm by calling /mfa/enable with a valid code."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Secret generated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<MfaSetupResponseResource> setupMfa(@AuthenticationPrincipal UserDetails principal) {
        var secret = userCommandService.handle(new SetupMfaCommand(principal.getUsername()));
        var otpAuthUrl = totpService.buildOtpAuthUrl("Veyra", principal.getUsername(), secret);
        return ResponseEntity.ok(new MfaSetupResponseResource(secret, otpAuthUrl));
    }

    @PostMapping("/enable")
    @Operation(
            summary = "Activate MFA",
            description = "Confirms MFA activation by verifying the first TOTP code from the authenticator app."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MFA activated"),
            @ApiResponse(responseCode = "400", description = "Invalid TOTP code or setup not initiated")
    })
    public ResponseEntity<?> enableMfa(@AuthenticationPrincipal UserDetails principal,
                                       @Valid @RequestBody MfaCodeResource resource) {
        try {
            var user = userCommandService.handle(new EnableMfaCommand(principal.getUsername(), resource.code()));
            if (user.isEmpty()) return ResponseEntity.badRequest().build();
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/disable")
    @Operation(
            summary = "Disable MFA",
            description = "Removes MFA from the authenticated user's account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MFA disabled"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> disableMfa(@AuthenticationPrincipal UserDetails principal) {
        var user = userCommandService.handle(new DisableMfaCommand(principal.getUsername()));
        if (user.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    @Operation(
            summary = "Verify TOTP code during sign-in",
            description = "Called after a sign-in that returned mfaRequired=true. Validates the TOTP code and returns the JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TOTP verified — JWT returned"),
            @ApiResponse(responseCode = "400", description = "Invalid TOTP code")
    })
    public ResponseEntity<?> verifyMfa(@Valid @RequestBody MfaVerifyResource resource) {
        try {
            var token = userCommandService.handle(new VerifyMfaCommand(resource.userId(), resource.code()));
            if (token.isEmpty()) return ResponseEntity.badRequest().build();
            record TokenResponse(String token) {}
            return ResponseEntity.ok(new TokenResponse(token.get()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
