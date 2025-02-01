package com.hardcodecoder.notes.account;

import com.hardcodecoder.notes.account.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@SpringBootTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(repository);
    }

    @Test
    void createNewAccountTest() {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var account = new Account(
            0,
            "Test Account",
            "email@test.com",
            "secret_password",
            now,
            now
        );

        var updatedAccount = repository.save(account);
        Assertions.assertNotNull(updatedAccount);
        Assertions.assertNotEquals(0, updatedAccount.id());
    }

    @Test
    void findByValidEmailTest() {
        var optionalAcc = repository.findByEmail("acc1@test.com");
        Assertions.assertNotNull(optionalAcc);
        Assertions.assertTrue(optionalAcc.isPresent());
        Assertions.assertNotNull(optionalAcc.get());
        Assertions.assertEquals("Test Account 1", optionalAcc.get().name());
    }

    @Test
    void findByInValidEmailTest() {
        var optionalAcc = repository.findByEmail("doesnotexist@test.com");
        Assertions.assertNotNull(optionalAcc);
        Assertions.assertFalse(optionalAcc.isPresent());
    }
}