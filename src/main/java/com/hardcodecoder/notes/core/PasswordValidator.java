package com.hardcodecoder.notes.core;

import org.springframework.lang.Nullable;

public class PasswordValidator implements DataValidator<String> {

    private static final byte MIN_PASSWORD_LENGTH = 8;

    @Override
    public boolean validate(@Nullable String password) {
        if (null == password || password.length() < MIN_PASSWORD_LENGTH) return false;

        var hasUpperChar = false;
        var hasLowerChar = false;
        var hasDigits = false;
        var hasSpaceChar = false;
        var hasSpecialChar = false;

        for (var c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperChar = true;
            else if (Character.isLowerCase(c)) hasLowerChar = true;
            else if (Character.isDigit(c)) hasDigits = true;
            else if (Character.isSpaceChar(c)) hasSpaceChar = true;
            else hasSpecialChar = true;
        }

        return hasUpperChar & hasLowerChar & hasDigits & !hasSpaceChar & hasSpecialChar;
    }
}