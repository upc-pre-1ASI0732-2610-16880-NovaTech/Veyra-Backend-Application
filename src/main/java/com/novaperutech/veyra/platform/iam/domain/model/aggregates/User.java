package com.novaperutech.veyra.platform.iam.domain.model.aggregates;

import com.novaperutech.veyra.platform.iam.domain.model.entities.Role;
import com.novaperutech.veyra.platform.iam.domain.model.valueobjects.MfaMethod;
import com.novaperutech.veyra.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User aggregate root
 * This class represents the aggregate root for the User entity.
 *
 * @see AuditableAbstractAggregateRoot
 */
@Getter
@Setter
@Entity
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(	name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Column(name = "totp_secret")
    private String totpSecret;

    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "mfa_method", nullable = false)
    private MfaMethod mfaMethod = MfaMethod.NONE;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "sms_mfa_code")
    private String smsMfaCode;

    @Column(name = "sms_mfa_code_expires_at")
    private LocalDateTime smsMfaCodeExpiresAt;

    /**
     * Default constructor.
     */
    public User() {
        this.roles = new HashSet<>();
    }

    /**
     * Constructor with username and password.
     * @param username the username
     * @param password the password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles = new HashSet<>();
    }

    public void enableMfa(String totpSecret) {
        this.totpSecret = totpSecret;
        this.mfaEnabled = true;
        this.mfaMethod = MfaMethod.TOTP;
    }

    public void disableMfa() {
        this.totpSecret = null;
        this.mfaEnabled = false;
        this.mfaMethod = MfaMethod.NONE;
        this.phoneNumber = null;
        this.smsMfaCode = null;
        this.smsMfaCodeExpiresAt = null;
    }

    /**
     * Begins SMS MFA setup: stores the phone number and a freshly sent code, pending
     * confirmation via {@link #activateSmsMfa()}. Does not enable MFA yet.
     */
    public void beginSmsMfaSetup(String phoneNumber, String code, LocalDateTime expiresAt) {
        this.phoneNumber = phoneNumber;
        this.smsMfaCode = code;
        this.smsMfaCodeExpiresAt = expiresAt;
        this.mfaMethod = MfaMethod.SMS;
    }

    /** Confirms SMS MFA setup after a valid code was verified. */
    public void activateSmsMfa() {
        this.mfaEnabled = true;
        this.smsMfaCode = null;
        this.smsMfaCodeExpiresAt = null;
    }

    /** Issues a fresh SMS code, e.g. for the second factor during sign-in. */
    public void refreshSmsCode(String code, LocalDateTime expiresAt) {
        this.smsMfaCode = code;
        this.smsMfaCodeExpiresAt = expiresAt;
    }

    public boolean isSmsCodeValid(String code) {
        return smsMfaCode != null
                && smsMfaCode.equals(code)
                && smsMfaCodeExpiresAt != null
                && LocalDateTime.now().isBefore(smsMfaCodeExpiresAt);
    }

    /**
     * Constructor with username, password, and roles.
     * @param username the username
     * @param password the password
     * @param roles the roles
     */
    public User(String username, String password, List<Role> roles) {
        this(username, password);
        addRoles(roles);
    }

    /**
     * Add a role to the user
     * @param role the role to add
     * @return the user with the added role
     */
    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    /**
     * Add a list of roles to the user
     * @param roles the list of roles to add
     * @return the user with the added roles
     */
    public User addRoles(List<Role> roles) {
        var validatedRoleSet = Role.validateRoleSet(roles);
        this.roles.addAll(validatedRoleSet);
        return this;
    }

}
