package com.hardcodecoder.notes.auth.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.OffsetDateTime;

@Table
public record Token(
    @Id long id,
    long accountId,
    @NonNull String accessToken,
    @Nullable String refreshToken,
    OffsetDateTime generatedOn
) {}