package com.hardcodecoder.notes.account;

import com.hardcodecoder.notes.account.model.Account;
import com.hardcodecoder.notes.core.EmailValidator;
import com.hardcodecoder.notes.core.PasswordValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountServiceTest {

    private final AccountRepository repository = mock();
    private final PasswordEncoder passwordEncoder = mock();
    private final AccountService service = new AccountService(
        repository,
        passwordEncoder,
        new EmailValidator(),
        new PasswordValidator()
    );

    @Test
    @DisplayName("Create request with valid input should return true")
    void verifyCreateWithValidInput() {
        var account = mock(Account.class);
        when(account.id())
            .thenReturn(1L);

        when(repository.save(any(Account.class)))
            .thenReturn(account);

        when(passwordEncoder.encode(anyString()))
            .thenReturn("EncodedPassword");

        var createdAccount = service.create("Test", "test@email.com", "Val1dP@ssword");
        Assertions.assertTrue(createdAccount.isPresent());
    }

    @Test
    @DisplayName("Create request failed with valid input should return false")
    void verifyCreateFailedWithValidInput() {
        var account = mock(Account.class);
        when(account.id())
            .thenReturn(0L);

        when(repository.save(any(Account.class)))
            .thenReturn(account);

        var createdAccount = service.create("Test", "test@email.com", "Val1dP@ssword");
        Assertions.assertFalse(createdAccount.isPresent());
    }

    @Test
    @DisplayName("Create request with invalid input should return false")
    void verifyCreateWithInvalidInput() {
        var account = service.create("Test", "email@.com", "Secret");
        Assertions.assertFalse(account.isPresent());
    }

    @Test
    @DisplayName("Verify Credentials request with valid input should return true")
    void verifyCredentialWithValidInput() {
        var email = "test@email.com";
        var password = "Val1dP@ssword";

        var account = mock(Account.class);
        when(account.password())
            .thenReturn(password);

        when(repository.findByEmail(eq(email)))
            .thenReturn(Optional.of(account));

        when(passwordEncoder.matches(anyString(), anyString()))
            .thenReturn(true);

        var response = service.verifyAccountCredentials(email, password);
        Assertions.assertTrue(response);
    }

    @Test
    @DisplayName("Verify Credentials request with valid input should return false")
    void verifyCredentialsWithInvalidInput() {
        var email = "test@email.com";
        var password = "0therP@ssword";

        var account = mock(Account.class);
        when(account.password())
            .thenReturn(password);

        when(repository.findByEmail(eq(email)))
            .thenReturn(Optional.of(account));

        when(passwordEncoder.matches(anyString(), anyString()))
            .thenReturn(false);

        var response = service.verifyAccountCredentials(email, password);
        Assertions.assertFalse(response);
    }

    @Test
    @DisplayName("Verify account exist check")
    void verifyAccountExistForExistingEmail() {
        var account = mock(Account.class);
        when(repository.findByEmail(eq("test@email.com")))
            .thenReturn(Optional.of(account));

        Assertions.assertTrue(service.checkAccountExist("test@email.com"));
        Assertions.assertFalse(service.checkAccountExist("newtest@email.com"));
    }
}