package com.hardcodecoder.notes.auth.model;

import org.springframework.lang.Nullable;

public record SignupRequest(
    @Nullable String name,
    @Nullable String email,
    @Nullable String password
) {}