package com.novaperutech.veyra.platform.iam.unit;

import com.novaperutech.veyra.platform.iam.application.internal.commandservices.UserCommandServiceImpl;
import com.novaperutech.veyra.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.novaperutech.veyra.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.novaperutech.veyra.platform.iam.domain.model.aggregates.User;
import com.novaperutech.veyra.platform.iam.domain.model.commands.SignInCommand;
import com.novaperutech.veyra.platform.iam.domain.model.commands.SignUpCommand;
import com.novaperutech.veyra.platform.iam.domain.model.entities.Role;
import com.novaperutech.veyra.platform.iam.domain.model.valueobjects.Roles;
import com.novaperutech.veyra.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.novaperutech.veyra.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IamAuthenticationUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashingService hashingService;

    @Mock
    private TokenService tokenService;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @Test
    void shouldAuthenticateUserSuccessfully() {
        var user = new User("jane", "encoded-password", List.of(new Role(Roles.ROLE_ADMIN)));
        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(user));
        when(hashingService.matches("plain-password", "encoded-password")).thenReturn(true);
        when(tokenService.generateToken("jane")).thenReturn("jwt-token");

        var result = userCommandService.handle(new SignInCommand("jane", "plain-password"));

        assertTrue(result.isPresent());
        assertEquals("jane", result.get().getLeft().getUsername());
        assertEquals("jwt-token", result.get().getRight());
        verify(userRepository).findByUsername("jane");
        verify(hashingService).matches("plain-password", "encoded-password");
        verify(tokenService).generateToken("jane");
    }

    @Test
    void shouldRejectInvalidCredentials() {
        var user = new User("jane", "encoded-password", List.of(new Role(Roles.ROLE_USER)));
        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(user));
        when(hashingService.matches("wrong-password", "encoded-password")).thenReturn(false);

        var exception = assertThrows(RuntimeException.class,
                () -> userCommandService.handle(new SignInCommand("jane", "wrong-password")));

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository).findByUsername("jane");
        verify(hashingService).matches("wrong-password", "encoded-password");
        verify(tokenService, never()).generateToken("jane");
    }

    @Test
    void shouldCreateEntitySuccessfully() {
        var requestedRole = new Role(Roles.ROLE_ADMIN);
        var persistedRole = new Role(1L, Roles.ROLE_ADMIN);
        var persistedUser = new User("john", "encoded-secret", List.of(persistedRole));

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(roleRepository.findByName(Roles.ROLE_ADMIN)).thenReturn(Optional.of(persistedRole));
        when(hashingService.encode("secret")).thenReturn("encoded-secret");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(persistedUser));

        var result = userCommandService.handle(new SignUpCommand("john", "secret", List.of(requestedRole)));

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
        verify(userRepository).existsByUsername("john");
        verify(roleRepository).findByName(Roles.ROLE_ADMIN);
        verify(hashingService).encode("secret");
        verify(userRepository).save(any(User.class));
        verify(userRepository).findByUsername("john");
    }

    @Test
    void shouldThrowExceptionWhenEntityDoesNotExist() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(roleRepository.findByName(Roles.ROLE_ADMIN)).thenReturn(Optional.empty());

        var exception = assertThrows(RuntimeException.class,
                () -> userCommandService.handle(new SignUpCommand("john", "secret", List.of(new Role(Roles.ROLE_ADMIN)))));

        assertEquals("Role name not found", exception.getMessage());
        verify(userRepository).existsByUsername("john");
        verify(roleRepository).findByName(Roles.ROLE_ADMIN);
        verify(userRepository, never()).save(any(User.class));
    }
}
