package com.hardcodecoder.notes.account;

import com.hardcodecoder.notes.account.model.Account;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(@NonNull AccountRepository accountRepository) {
        repository = accountRepository;
    }

    public boolean create(
        @Nullable String name,
        @NonNull String email,
        @NonNull String password
    ) {
        try {
            var nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
            var account = repository.save(new Account(
                0,
                name,
                email,
                password,
                nowUtc,
                nowUtc
            ));

            return account.id() != 0;
        } catch (Exception _) {
            return false;
        }
    }
}