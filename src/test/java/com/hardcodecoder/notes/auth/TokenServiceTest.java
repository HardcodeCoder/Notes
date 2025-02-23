package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.model.Account;
import com.hardcodecoder.notes.auth.model.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TokenServiceTest {

    private final TokenRepository repository = mock();
    private final TokenService service = new TokenService(repository, 100);

    @Test
    @DisplayName("Verify token is created for valid account")
    void verifyTokenIsCreated() {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var account = new Account(100, "Name", "test@email.com", "P@88word", now, now);

        when(repository.save(any()))
            .thenReturn(new Token(1, 100, "access_token", "refresh_token", now));

        var token = service.createToken(account);
        Assertions.assertTrue(token.isPresent());
    }

    @Test
    @DisplayName("Verify existing token gets deleted when creating new one")
    void verifyExistingTokenIsDeletedWhenCreatingNew() {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var account = new Account(100, "Name", "test@email.com", "P@88word", now, now);
        var token = new Token(1, 100, "access_token", "refresh_token", now);

        when(repository.findByAccountId(eq(100L)))
            .thenReturn(Optional.of(token));

        when(repository.save(any()))
            .thenReturn(token);

        service.createToken(account);

        verify(repository, times(1)).delete(token);
    }

    @Test
    @DisplayName("Verify valid access tokens are detected")
    void verifyValidAccessTokenIsDetected() {
        Assertions.assertTrue(service.isAccessToken("acc_token"));
    }

    @Test
    @DisplayName("Verify invalid access tokens are not detected")
    void verifyInvalidAccessTokenIsNotDetected() {
        Assertions.assertFalse(service.isAccessToken("act_token"));
    }

    @Test
    @DisplayName("Verify valid refresh tokens are detected")
    void verifyValidRefreshTokenIsDetected() {
        Assertions.assertTrue(service.isRefreshToken("ref_token"));
    }

    @Test
    @DisplayName("Verify invalid refresh tokens are not detected")
    void verifyInvalidRefreshTokenIsNotDetected() {
        Assertions.assertFalse(service.isRefreshToken("ret_token"));
    }

    @Test
    @DisplayName("Verify existing token is updated using refresh token")
    void verifyTokenUpdate() {
        var token = mock(Token.class);
        var account = mock(Account.class);

        when(token.id())
            .thenReturn(100L);

        when(token.refreshToken())
            .thenReturn("ref_token");

        when(repository.findByAccountId(1))
            .thenReturn(Optional.of(token));

        when(repository.save(any()))
            .thenReturn(token);

        when(account.id())
            .thenReturn(1L);

        service.updateToken(account, "ref_token");
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Verify invalid refresh token should not update")
    void verifyTokenNotUpdatedWhenRefreshTokenInvalid() {
        var token = mock(Token.class);

        when(token.refreshToken())
            .thenReturn("ref_token");

        when(repository.findByAccountId(anyLong()))
            .thenReturn(Optional.of(token));

        var updatedToken = service.updateToken(mock(Account.class), "ref_token2");
        Assertions.assertFalse(updatedToken.isPresent());
    }

    @Test
    @DisplayName("Verify valid access token is recognised")
    void verifyValidAccessTokenIsRecognised() {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var token = new Token(100, 1, "acc_token", "ref_token", now);

        when(repository.findByAccountId(eq(1L)))
            .thenReturn(Optional.of(token));

        Assertions.assertTrue(service.isAccessTokenValid("acc_token", 1L));
    }

    @Test
    @DisplayName("Verify expired access token is not recognised")
    void verifyExpiredAccessTokenIsRecognised() {
        var now = OffsetDateTime.now(ZoneOffset.UTC).minus(200, ChronoUnit.MILLIS);
        var token = new Token(100, 1, "acc_token", "ref_token", now);

        when(repository.findByAccountId(eq(1L)))
            .thenReturn(Optional.of(token));

        Assertions.assertFalse(service.isAccessTokenValid("acc_token", 1L));
    }
}