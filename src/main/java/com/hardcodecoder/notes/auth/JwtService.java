package com.hardcodecoder.notes.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final String secretSigningKey;
    private final long validityDuration;

    public JwtService(@NonNull String base64EncodedKey, long validityDurationMills) {
        secretSigningKey = base64EncodedKey;
        validityDuration = validityDurationMills;
    }

    @NonNull
    public String generateToken(@NonNull String subject) {
        var now = System.currentTimeMillis();
        return Jwts
            .builder()
            .subject(subject)
            .issuedAt(new Date(now))
            .expiration(new Date(now + validityDuration))
            .signWith(secretKey())
            .compact();
    }

    public boolean isTokenValid(@NonNull String token, @NonNull String subject) {
        try {
            var claims = extractClaims(token);
            return claims.getExpiration().after(new Date(System.currentTimeMillis())) &&
                   subject.equals(claims.getSubject());
        } catch (Exception _) {
            return false;
        }
    }

    @NonNull
    public String extractSubject(@NonNull String token) throws JwtException {
        return extractClaims(token).getSubject();
    }

    @NonNull
    public Date extractExpiration(@NonNull String token) throws JwtException {
        return extractClaims(token).getExpiration();
    }

    @NonNull
    private Claims extractClaims(@NonNull String token) throws JwtException {
        return Jwts
            .parser()
            .verifyWith(secretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    @NonNull
    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretSigningKey));
    }
}