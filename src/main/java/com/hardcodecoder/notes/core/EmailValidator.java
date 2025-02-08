package com.hardcodecoder.notes.core;

import org.springframework.lang.Nullable;

public class EmailValidator implements DataValidator<String> {

    @Override
    public boolean validate(@Nullable String email) {
        return null != email && email.matches("^[^@]+@[^@]+\\.[^@]+$");
    }
}