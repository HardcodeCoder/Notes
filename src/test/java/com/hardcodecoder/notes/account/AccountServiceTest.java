package com.hardcodecoder.notes.account;

import com.hardcodecoder.notes.account.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountServiceTest {

    private final AccountRepository repository = Mockito.mock();
    private final AccountService service = new AccountService(repository);

    @Test
    void shouldReturnTrueWhenIdIsNotZero() {
        var account = Mockito.mock(Account.class);
        Mockito
            .when(account.id())
            .thenReturn(1L);

        Mockito
            .when(repository.save(Mockito.any(Account.class)))
            .thenReturn(account);

        var result = service.create("", "", "");
        Assertions.assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenIdIsZero() {
        var account = Mockito.mock(Account.class);
        Mockito
            .when(account.id())
            .thenReturn(0L);

        Mockito
            .when(repository.save(Mockito.any(Account.class)))
            .thenReturn(account);

        var result = service.create("", "", "");
        Assertions.assertFalse(result);
    }
}