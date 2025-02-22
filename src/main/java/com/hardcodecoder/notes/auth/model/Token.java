package com.hardcodecoder.notes.auth.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.time.OffsetDateTime;

@Table
public record Token(
    @Id long id,
    long accountId,
    @NonNull String accessToken,
    @NonNull String refreshToken,
    @NonNull OffsetDateTime generatedOn
) {}