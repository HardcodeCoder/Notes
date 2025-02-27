package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.AccountService;
import com.hardcodecoder.notes.account.model.Account;
import com.hardcodecoder.notes.auth.model.AuthResult;
import com.hardcodecoder.notes.auth.model.SignupRequest;
import com.hardcodecoder.notes.auth.model.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthServiceTest {

    private final AccountService accountService = mock();
    private final TokenService tokenService = mock();
    private final AuthService service = new AuthService(
        accountService,
        tokenService
    );

    @Test
    @DisplayName("Signup request with valid input should return success response")
    void verifyValidSignupRequest() {
        var account = mock(Account.class);
        when(accountService.create(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(account));

        var token = mock(Token.class);
        when(tokenService.createToken(any()))
            .thenReturn(Optional.of(token));

        var request = new SignupRequest("Test", "test@email.com", "Val1dP@ssword");
        var response = service.processSignUpRequest(request);

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

        Assertions.assertInstanceOf(AuthResult.Error.class, response);
    }

    @Test
    @DisplayName("Signup request with null email should return failed response")
    void verifySignupRequestWhenEmailIsNull() {
        var request = new SignupRequest("Test", null, "Val1dP@ssword");
        var response = service.processSignUpRequest(request);

        Assertions.assertInstanceOf(AuthResult.Error.class, response);
    }

    @Test
    @DisplayName("Signup request with null password should return failed response")
    void verifySignupRequestWhenPasswordIsNull() {
        var request = new SignupRequest("Test", "test@email.com", null);
        var response = service.processSignUpRequest(request);

        Assertions.assertInstanceOf(AuthResult.Error.class, response);
    }

    @Test
    @DisplayName("Verify access token is refreshed with valid refresh token")
    void refreshAccessTokenWithValidRefreshToken() {
        var account = mock(Account.class);
        when(accountService.findById(anyLong()))
            .thenReturn(Optional.of(account));

        var token = mock(Token.class);
        when(tokenService.updateToken(same(account), eq("ref_token")))
            .thenReturn(Optional.of(token));

        when(tokenService.isRefreshToken(anyString()))
            .thenReturn(true);

        var response = service.refreshAccessToken("Bearer ref_token", 100L);
        Assertions.assertInstanceOf(AuthResult.Success.class, response);
    }

    @Test
    @DisplayName("Verify access token not is refreshed when refresh token is invalid")
    void verifyRetrieveWhenBasicAuthTokenIsInvalid() {
        var response = service.refreshAccessToken("Bearer token", 100L);
        Assertions.assertInstanceOf(AuthResult.Error.class, response);

        response = service.refreshAccessToken("Bearer", 100L);
        Assertions.assertInstanceOf(AuthResult.Error.class, response);

        response = service.refreshAccessToken("Bearer ref_", 100L);
        Assertions.assertInstanceOf(AuthResult.Error.class, response);
    }
}