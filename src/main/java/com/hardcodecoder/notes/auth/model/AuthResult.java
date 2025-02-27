package com.hardcodecoder.notes.auth.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.OffsetDateTime;

public sealed interface AuthResult {

    record Success(
        @NonNull String accessToken,
        @NonNull String refreshToken,
        @NonNull OffsetDateTime expiresOn
    ) implements AuthResult {}

    record Error(
        @Nullable String message
    ) implements AuthResult {}
}