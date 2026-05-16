package com.novaperutech.veyra.platform.iam.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaperutech.veyra.platform.iam.domain.model.aggregates.User;
import com.novaperutech.veyra.platform.iam.domain.model.entities.Role;
import com.novaperutech.veyra.platform.iam.domain.model.valueobjects.Roles;
import com.novaperutech.veyra.platform.iam.domain.services.UserCommandService;
import com.novaperutech.veyra.platform.iam.interfaces.rest.AuthenticationController;
import com.novaperutech.veyra.platform.iam.interfaces.rest.resources.SignInResource;
import com.novaperutech.veyra.platform.iam.interfaces.rest.resources.SignUpResource;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IamAuthenticationControllerIntegrationTest {

    @Mock
    private UserCommandService userCommandService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthenticationController(userCommandService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldAuthenticateUserSuccessfully() throws Exception {
        var user = new User("john", "encoded", List.of(new Role(Roles.ROLE_ADMIN)));
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userCommandService.handle(any(com.novaperutech.veyra.platform.iam.domain.model.commands.SignInCommand.class)))
                .thenReturn(Optional.of(ImmutablePair.of(user, "jwt-token")));

        mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignInResource("john", "secret"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(userCommandService).handle(any(com.novaperutech.veyra.platform.iam.domain.model.commands.SignInCommand.class));
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        when(userCommandService.handle(any(com.novaperutech.veyra.platform.iam.domain.model.commands.SignInCommand.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignInResource("john", "wrong-secret"))))
                .andExpect(status().isNotFound());

        verify(userCommandService).handle(any(com.novaperutech.veyra.platform.iam.domain.model.commands.SignInCommand.class));
    }

    @Test
    void shouldCreateEntitySuccessfully() throws Exception {
        var user = new User("john", "encoded", List.of(new Role(Roles.ROLE_USER)));
        ReflectionTestUtils.setField(user, "id", 2L);
        when(userCommandService.handle(any(com.novaperutech.veyra.platform.iam.domain.model.commands.SignUpCommand.class)))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SignUpResource("john", "secret", List.of("ROLE_USER")))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

        verify(userCommandService).handle(any(com.novaperutech.veyra.platform.iam.domain.model.commands.SignUpCommand.class));
    }

    @Test
    void shouldRejectInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userCommandService);
    }
}
