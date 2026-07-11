package com.novaperutech.veyra.platform.iam.interfaces.rest;

import com.novaperutech.veyra.platform.iam.domain.model.commands.*;
import com.novaperutech.veyra.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.novaperutech.veyra.platform.iam.domain.model.queries.GetUserByUsernameQuery;
import com.novaperutech.veyra.platform.iam.domain.services.UserCommandService;
import com.novaperutech.veyra.platform.iam.domain.services.UserQueryService;
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
    private final UserQueryService userQueryService;
    private final TotpService totpService;

    public MfaController(UserCommandService userCommandService, UserQueryService userQueryService, TotpService totpService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.totpService = totpService;
    }

    @GetMapping("/status")
    @Operation(
            summary = "Get current MFA status",
            description = "Returns whether MFA is enabled for the authenticated user and which method (TOTP/SMS) is active."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<MfaStatusResource> getStatus(@AuthenticationPrincipal UserDetails principal) {
        var user = userQueryService.handle(new GetUserByUsernameQuery(principal.getUsername()))
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(new MfaStatusResource(user.isMfaEnabled(), user.getMfaMethod().name()));
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

    @PostMapping("/sms/setup")
    @Operation(
            summary = "Initialize SMS MFA setup",
            description = "Stores the phone number and sends the first SMS verification code. " +
                    "MFA is NOT active yet — confirm by calling /mfa/enable with the received code."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code sent"),
            @ApiResponse(responseCode = "400", description = "Invalid phone number"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> setupSmsMfa(@AuthenticationPrincipal UserDetails principal,
                                         @Valid @RequestBody SetupSmsMfaResource resource) {
        try {
            var user = userCommandService.handle(new SetupSmsMfaCommand(principal.getUsername(), resource.phoneNumber()));
            if (user.isEmpty()) return ResponseEntity.badRequest().build();
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/enable")
    @Operation(
            summary = "Activate MFA",
            description = "Confirms MFA activation by verifying the first TOTP or SMS code."
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
            summary = "Verify TOTP/SMS code during sign-in",
            description = "Called after a sign-in that returned mfaRequired=true. Validates the code and returns the same " +
                    "authenticated-user resource shape as sign-in (id, username, roles, token), so the client can fully " +
                    "restore session state without relying on stale data from before the MFA challenge."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code verified — authenticated user resource returned"),
            @ApiResponse(responseCode = "400", description = "Invalid code")
    })
    public ResponseEntity<?> verifyMfa(@Valid @RequestBody MfaVerifyResource resource) {
        try {
            var token = userCommandService.handle(new VerifyMfaCommand(resource.userId(), resource.code()));
            if (token.isEmpty()) return ResponseEntity.badRequest().build();
            var user = userQueryService.handle(new GetUserByIdQuery(resource.userId()))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(user, token.get()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
