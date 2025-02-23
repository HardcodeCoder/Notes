package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.model.Account;
import com.hardcodecoder.notes.auth.model.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@Service
public class TokenService {

    private static final String ACCESS_TOKEN_PREFIX = "acc_";
    private static final String REFRESH_TOKEN_PREFIX = "ref_";

    private final Random random = new Random();
    private final TokenRepository repository;
    private final long expiresInMills;

    public TokenService(
        @NonNull TokenRepository repository,
        @Value("${notes.token.expiresInMills}") long expiresInMills
    ) {
        this.repository = repository;
        this.expiresInMills = expiresInMills;
    }

    @NonNull
    public Optional<Token> createToken(@NonNull Account account) {
        var existingToken = repository.findByAccountId(account.id());
        existingToken.ifPresent(repository::delete);

        var accessToken = generateAccessToken(account);
        var refreshToken = generateRefreshToken(account);

        if (null != accessToken && null != refreshToken) {
            var token = repository.save(new Token(
                0,
                account.id(),
                accessToken,
                refreshToken,
                OffsetDateTime.now(ZoneOffset.UTC)
            ));

            if (token.id() > 0) return Optional.of(token);
        }

        return Optional.empty();
    }

    @NonNull
    public Optional<Token> updateToken(@NonNull Account account, @NonNull String refreshToken) {
        if (isRefreshToken(refreshToken)) {
            var existingToken = repository.findByAccountId(account.id());

            if (existingToken.isPresent() && existingToken.get().refreshToken().equals(refreshToken)) {
                var accessToken = generateAccessToken(account);

                if (null != accessToken) {
                    var token = repository.save(new Token(
                        existingToken.get().id(),
                        account.id(),
                        accessToken,
                        refreshToken,
                        OffsetDateTime.now(ZoneOffset.UTC)
                    ));
                    return Optional.of(token);
                }
            }
        }

        return Optional.empty();
    }

    public boolean isAccessTokenValid(@NonNull String accessToken, long accountId) {
        if (isAccessToken(accessToken)) {
            var token = repository.findByAccountId(accountId);
            return token.isPresent() &&
                   token.get().accessToken().equals(accessToken) &&
                   expiryTime(token.get()).isAfter(OffsetDateTime.now(ZoneOffset.UTC));
        }
        return false;
    }

    @NonNull
    public OffsetDateTime expiryTime(@NonNull Token token) {
        return token.generatedOn().plus(expiresInMills, ChronoUnit.MILLIS);
    }

    public boolean isAccessToken(@NonNull String token) {
        return token.length() > ACCESS_TOKEN_PREFIX.length() &&
               token.startsWith(ACCESS_TOKEN_PREFIX);
    }

    public boolean isRefreshToken(@NonNull String token) {
        return token.length() > REFRESH_TOKEN_PREFIX.length() &&
               token.startsWith(REFRESH_TOKEN_PREFIX);
    }

    @Nullable
    private String generateAccessToken(@NonNull Account account) {
        return generateToken(account, "SHA-256", ACCESS_TOKEN_PREFIX);
    }

    @Nullable
    private String generateRefreshToken(@NonNull Account account) {
        return generateToken(account, "MD5", REFRESH_TOKEN_PREFIX);
    }

    @Nullable
    private String generateToken(@NonNull Account account, @NonNull String hashAlgorithm, @NonNull String prefix) {
        try {
            var messageDigest = MessageDigest.getInstance(hashAlgorithm);
            var data = account.toString() + expiresInMills + random.nextLong();
            var tokenDigest = messageDigest.digest(data.getBytes(StandardCharsets.UTF_8));
            return prefix + Base64.getEncoder().withoutPadding().encodeToString(tokenDigest);
        } catch (NoSuchAlgorithmException _) {}
        return null;
    }
}