package com.hardcodecoder.notes.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Base64;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtServiceTest {

    private static final String tokenSubject = "test@email.com";
    private final String secretKey;

    public JwtServiceTest() {
        secretKey = Base64.getEncoder().encodeToString(
            Jwts.SIG.HS512.key().build().getEncoded()
        );
    }

    @Test
    @DisplayName("Verify generated tokens are valid")
    void verifyGeneratedTokensAreValid() {
        var service = new JwtService(secretKey, 5000);
        var token = service.generateToken(tokenSubject);

        Assertions.assertTrue(service.isTokenValid(token, tokenSubject));
    }

    @Test
    @DisplayName("Verify expired token throws ExpiredJwtException")
    void verifyExpiredTokenThrows() {
        var service = new JwtService(secretKey, 1);
        var token = service.generateToken(tokenSubject);

        Assertions.assertThrows(ExpiredJwtException.class, () -> service.extractExpiration(token));
    }

    @Test
    @DisplayName("Verity extract subject is correct for valid token")
    void verifySubjectForValidToken() {
        var service = new JwtService(secretKey, 1000);
        var token = service.generateToken(tokenSubject);

        Assertions.assertEquals(tokenSubject, service.extractSubject(token));
    }

    @Test
    @DisplayName("Verity extract expiration date is correct for valid token")
    void verifyExpirationForValidToken() {
        var service = new JwtService(secretKey, 1000);
        var token = service.generateToken(tokenSubject);

        Assertions.assertNotNull(service.extractExpiration(token));
    }
}