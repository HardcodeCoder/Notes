package com.hardcodecoder.notes.account;

import com.hardcodecoder.notes.account.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class AccountJsonTest {

    @Autowired
    private JacksonTester<Account> jacksonTester;

    @Test
    @DisplayName("Verify account is serialized without password field")
    void verifyAccountSerialization() throws IOException {
        var nowStr = "2025-03-01T20:16:10.5Z";
        var now = OffsetDateTime.parse(nowStr);
        var account = new Account(
            100L,
            "Account name",
            "test@email.com",
            "Password",
            now,
            now
        );
        var json = jacksonTester.write(account);

        assertThat(json).hasJsonPath("@.id");
        assertThat(json).hasJsonPath("@.name");
        assertThat(json).hasJsonPath("@.email");
        assertThat(json).hasJsonPath("@.createdOn");
        assertThat(json).hasJsonPath("@.modifiedOn");

        assertThat(json).extractingJsonPathNumberValue("@.id").isEqualTo(100);
        assertThat(json).extractingJsonPathStringValue("@.name").isEqualTo("Account name");
        assertThat(json).extractingJsonPathStringValue("@.email").isEqualTo("test@email.com");
        assertThat(json).extractingJsonPathStringValue("@.createdOn").isEqualTo(nowStr);
        assertThat(json).extractingJsonPathStringValue("@.modifiedOn").isEqualTo(nowStr);

        assertThat(json).doesNotHaveJsonPath("@.password");
    }
}