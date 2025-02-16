package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.AccountRepository;
import com.hardcodecoder.notes.account.model.Account;
import com.hardcodecoder.notes.auth.model.Token;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;

@SpringBootTest
public class TokenRepositoryTest {

    @Autowired
    private TokenRepository repository;

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;

    @BeforeEach
    void setup() {
        Assertions.assertNotNull(repository);

        var now = OffsetDateTime.now();
        testAccount = accountRepository.save(new Account(
            0,
            "Token Test Account",
            "token_acc@test.com",
            "password",
            now,
            now
        ));

        Assertions.assertNotNull(testAccount);
        Assertions.assertTrue(testAccount.id() > 0);
    }

    @Test
    @DisplayName("Verify new tokens are persisted")
    void verifyCreateTokenAndSave() {
        var token = repository.save(new Token(
            0,
            testAccount.id(),
            "access_token",
            "refresh_token",
            OffsetDateTime.now()
        ));

        Assertions.assertNotNull(token);
        Assertions.assertTrue(token.id() > 0);
    }

    @Test
    @DisplayName("Verify tokens gets fetched using account id")
    void verifyFindTokenByAccountId() {
        var token = repository.save(new Token(
            0,
            testAccount.id(),
            "access_token",
            "refresh_token",
            OffsetDateTime.now()
        ));

        var tokenByAccountId = repository.findByAccountId(testAccount.id());
        Assertions.assertTrue(tokenByAccountId.isPresent());
        Assertions.assertEquals(token.id(), tokenByAccountId.get().id());
    }

    @Test
    @DisplayName("Verify tokens gets deleted with account")
    void verifyTokensGetDeletedWithAccount() {
        var token = repository.save(new Token(
            0,
            testAccount.id(),
            "access_token",
            "refresh_token",
            OffsetDateTime.now()
        ));

        accountRepository.delete(testAccount);

        var deletedToken = repository.findById(token.id());
        Assertions.assertFalse(deletedToken.isPresent());
    }

    @AfterEach
    void cleanup() {
        accountRepository.delete(testAccount);
    }
}