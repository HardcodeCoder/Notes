package com.hardcodecoder.notes.auth.validator;

import com.hardcodecoder.notes.core.DataValidator;
import org.springframework.lang.Nullable;

public class EmailValidator implements DataValidator<String> {

    @Override
    public boolean validate(@Nullable String email) {
        return null != email && email.matches("^[^@]+@[^@]+\\.[^@]+$");
    }
}