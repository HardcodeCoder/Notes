package com.hardcodecoder.notes.auth.model;

import org.springframework.lang.Nullable;

public record AuthResponse(
    @Nullable String message,
    boolean success
) {}