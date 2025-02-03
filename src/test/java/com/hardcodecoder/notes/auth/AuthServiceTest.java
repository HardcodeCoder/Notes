package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.AccountService;
import com.hardcodecoder.notes.account.model.Account;
import com.hardcodecoder.notes.auth.model.SignupRequest;
import com.hardcodecoder.notes.auth.validator.EmailValidator;
import com.hardcodecoder.notes.auth.validator.PasswordValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthServiceTest {

    private final AccountService accountService = mock();
    private final PasswordEncoder passwordEncoder = mock();
    private final AuthService service = new AuthService(
        accountService,
        passwordEncoder,
        new EmailValidator(),
        new PasswordValidator()
    );

    @Test
    @DisplayName("When signup request is valid should return success response")
    void shouldReturnSuccessForValidRequest() {
        when(accountService.create(anyString(), anyString(), anyString()))
            .thenReturn(true);
        when(passwordEncoder.encode(anyString()))
            .thenReturn("EncodedPassword");

        var request = new SignupRequest("Test", "test@email.com", "Val1dP@ssword");
        var response = service.processSignUpRequest(request);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.success());
        Assertions.assertEquals("Request processed", response.message());
    }

    @Test
    @DisplayName("When Email is null should return failed response")
    void shouldReturnFailedWhenEmailIsNull() {
        var request = new SignupRequest("Test", null, "Val1dP@ssword");
        var response = service.processSignUpRequest(request);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.success());
        Assertions.assertEquals("Invalid Request", response.message());
    }

    @Test
    @DisplayName("When Password is null should return failed response")
    void shouldReturnFailedWhenPasswordIsNull() {
        var request = new SignupRequest("Test", "test@email.com", null);
        var response = service.processSignUpRequest(request);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.success());
        Assertions.assertEquals("Invalid Request", response.message());
    }

    @Test
    @DisplayName("Given valid basic auth token should return success")
    void shouldReturnSuccessForValidLoginAttempt() {
        var account = mock(Account.class);
        when(account.password())
            .thenReturn("8ecureP@ss");

        when(passwordEncoder.matches(anyString(), anyString()))
            .thenReturn(true);

        when(accountService.findByEmail(anyString()))
            .thenReturn(Optional.of(account));

        var authToken = Base64.getEncoder().encodeToString("test@email.com:8ecureP@ss".getBytes());
        var response = service.processLoginRequest("Basic " + authToken);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.success());
    }

    @Test
    @DisplayName("Given non existent credentials should return failed response")
    void shouldReturnFailedForNonExistentCredentials() {
        var account = mock(Account.class);
        when(account.password())
            .thenReturn("DifferentPassword");

        when(passwordEncoder.matches(eq("8ecureP@ss"), anyString()))
            .thenReturn(false);

        when(accountService.findByEmail(anyString()))
            .thenReturn(Optional.of(account));

        var authToken = Base64.getEncoder().encodeToString("test@email.com:8ecureP@ss".getBytes());
        var response = service.processLoginRequest("Basic " + authToken);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.success());
    }

    @Test
    @DisplayName("Given invalid basic auth token should return failed response")
    void shouldReturnFailedForInvalidToken() {
        var authToken = Base64.getEncoder().encodeToString("test@email.com:8ecureP@ss".getBytes());
        var response = service.processLoginRequest(authToken);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.success());
    }

    @Test
    @DisplayName("Given incomplete basic auth token should return failed response")
    void shouldReturnFailedForIncompleteToken() {
        var authToken = Base64.getEncoder().encodeToString("test@email.com:".getBytes());
        var response = service.processLoginRequest("Basic " + authToken);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.success());
        Assertions.assertEquals("Invalid auth token", response.message());
    }
}