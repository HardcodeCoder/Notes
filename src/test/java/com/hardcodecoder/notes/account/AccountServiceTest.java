package com.hardcodecoder.notes.account;

import com.hardcodecoder.notes.account.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountServiceTest {

    private final AccountRepository repository = Mockito.mock();
    private final AccountService service = new AccountService(repository);

    @Test
    @DisplayName("Given valid inputs when account is created should return true")
    void shouldReturnTrueWhenIdIsNotZero() {
        var account = Mockito.mock(Account.class);
        when(account.id())
            .thenReturn(1L);

        when(repository.save(Mockito.any(Account.class)))
            .thenReturn(account);

        var result = service.create("Test", "test@email.com", "Password");
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("When account is not created should return false")
    void shouldReturnFalseWhenIdIsZero() {
        var account = Mockito.mock(Account.class);
        when(account.id())
            .thenReturn(0L);

        when(repository.save(Mockito.any(Account.class)))
            .thenReturn(account);

        var result = service.create("", "", "");
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Given a valid email should return the corresponding account")
    void shouldReturnAccountIfValidEmail() {
        var testEmail = "test@email.com";
        var account = Mockito.mock(Account.class);
        when(repository.findByEmail(eq(testEmail)))
            .thenReturn(Optional.of(account));

        var optionalAccount = service.findByEmail(testEmail);
        Assertions.assertNotNull(optionalAccount);
        Assertions.assertTrue(optionalAccount.isPresent());
    }

    @Test
    @DisplayName("Given an invalid email should return empty optional")
    void shouldReturnEmptyIfInvalidEmail() {
        var account = Mockito.mock(Account.class);
        when(repository.findByEmail(eq("test@email.com")))
            .thenReturn(Optional.of(account));

        var optionalAccount = service.findByEmail("doesnotexist@email.com");
        Assertions.assertNotNull(optionalAccount);
        Assertions.assertFalse(optionalAccount.isPresent());
    }
}