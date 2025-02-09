package com.hardcodecoder.notes.auth.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;

public sealed interface AuthResult {

    record Success(
        @NonNull String token,
        @NonNull Date expiresOn
    ) implements AuthResult {}

    record Error(
        @Nullable String message
    ) implements AuthResult {}
}