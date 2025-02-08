package com.hardcodecoder.notes.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @ParameterizedTest()
    @ValueSource(strings = {
        "valid.email@test.com",
        "valid_email@test.com",
        "valid-email@test.com",
        "01valid@test.com",
        "valid-01@test.com"
    })
    void validateAlphanumericEmails(String email) {
        Assertions.assertTrue(validator.validate(email));
    }

    @ParameterizedTest()
    @ValueSource(strings = {
        "@invalidemail@test.com",
        "invalidemail@test.@om",
        "invalidemail@te@t.com",
        ""
    })
    void invalidEmailTest(String email) {
        Assertions.assertFalse(validator.validate(email));
    }

    @Test
    void shouldNotValidateNullEmail() {
        Assertions.assertFalse(validator.validate(null));
    }
}