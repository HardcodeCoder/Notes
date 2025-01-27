package com.hardcodecoder.notes.account.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.OffsetDateTime;

@Table
public record Account(
    @Id long id,
    @Nullable String name,
    @NonNull String email,
    @NonNull String password,
    @NonNull OffsetDateTime createdOn,
    @NonNull OffsetDateTime modifiedOn
) {}