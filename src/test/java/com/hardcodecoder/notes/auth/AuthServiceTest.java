package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.AccountService;
import com.hardcodecoder.notes.account.model.Account;
import com.hardcodecoder.notes.auth.model.AuthResult;
import com.hardcodecoder.notes.auth.model.SignupRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Base64;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthServiceTest {

    private final AccountService accountService = mock();
    private final JwtService jwtService = mock();
    private final AuthService service = new AuthService(
        accountService,
        jwtService
    );

    @Test
    @DisplayName("Signup request with valid input should return success response")
    void verifyValidSignupRequest() {
        var account = mock(Account.class);
        when(accountService.create(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(account));

        var request = new SignupRequest("Test", "test@email.com", "Val1dP@ssword");
        var response = service.processSignUpRequest(request);

        Assertions.assertNotNull(response);
        Assertions.assertInstanceOf(AuthResult.Success.class, response);
    }

    @Test
    @DisplayName("Duplicate signup request with valid input should return error response")
    void verifyDuplicateValidSignupRequest() {
        var account = mock(Account.class);
        when(accountService.create(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(account));

        when(accountService.checkAccountExist(anyString()))
            .thenReturn(true);

        var request = new SignupRequest("Test", "test@email.com", "Val1dP@ssword");
        var response = service.processSignUpRequest(request);

        Assertions.assertNotNull(response);
        Assertions.assertInstanceOf(AuthResult.Error.class, response);
    }

    @Test
    @DisplayName("Signup request with null email should return failed response")
    void verifySignupRequestWhenEmailIsNull() {
        var request = new SignupRequest("Test", null, "Val1dP@ssword");
        var response = service.processSignUpRequest(request);

        Assertions.assertNotNull(response);
        Assertions.assertInstanceOf(AuthResult.Error.class, response);
    }

    @Test
    @DisplayName("Signup request with null password should return failed response")
    void verifySignupRequestWhenPasswordIsNull() {
        var request = new SignupRequest("Test", "test@email.com", null);
        var response = service.processSignUpRequest(request);

        Assertions.assertNotNull(response);
        Assertions.assertInstanceOf(AuthResult.Error.class, response);
    }

    @Test
    @DisplayName("Retrieve token with basic auth should return success response")
    void verifyRetrieveWithValidBasicAuthToken() {
        var email = "test@email.com";
        var password = "8ecureP@ss";

        var account = mock(Account.class);
        when(account.password())
            .thenReturn(password);

        when(accountService.verifyAccountCredentials(eq(email), eq(password)))
            .thenReturn(true);

        var authToken = Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
        var response = service.processLoginRequest("Basic " + authToken);

        Assertions.assertNotNull(response);
        Assertions.assertInstanceOf(AuthResult.Success.class, response);
    }

    @Test
    @DisplayName("Retrieve token with invalid basic auth should return failed response")
    void verifyRetrieveWhenBasicAuthTokenIsInvalid() {
        var authToken = Base64.getEncoder().encodeToString("test@email.com:8ecureP@ss".getBytes());
        var response = service.processLoginRequest(authToken);

        Assertions.assertNotNull(response);
        Assertions.assertInstanceOf(AuthResult.Error.class, response);
    }

    @Test
    @DisplayName("Retrieve token with incomplete basic auth should return failed response")
    void verifyRetrieveWhenBasicAuthTokenIsIncomplete() {
        var authToken = Base64.getEncoder().encodeToString("test@email.com:".getBytes());
        var response = service.processLoginRequest("Basic " + authToken);

        Assertions.assertNotNull(response);
        Assertions.assertInstanceOf(AuthResult.Error.class, response);
    }
}