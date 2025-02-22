package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.model.Account;
import com.hardcodecoder.notes.auth.model.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
}