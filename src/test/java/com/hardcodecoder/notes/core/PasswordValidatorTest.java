package com.hardcodecoder.notes.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @ParameterizedTest
    @ValueSource(strings = {
        "SuperStrong@Passw0rd",
        "_..123Abc#"
    })
    void shouldGetValidated(String password) {
        Assertions.assertTrue(validator.validate(password));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1#Bc",
        "SuperStrong123",
        "OnlyAlphabets",
        "12345678",
        "_..(){}##$$@@",
        ""
    })
    void shouldNotGetValidated(String password) {
        Assertions.assertFalse(validator.validate(password));
    }

    @Test
    void shouldNotValidateNullPassword() {
        Assertions.assertFalse(validator.validate(null));
    }
}