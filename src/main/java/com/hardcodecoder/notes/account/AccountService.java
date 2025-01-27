package com.hardcodecoder.notes.account;

import com.hardcodecoder.notes.account.model.Account;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class AccountService {

    private final AccountRepository repository;
    private final PasswordEncoder encoder;

    public AccountService(
        @NonNull AccountRepository accountRepository,
        @NonNull PasswordEncoder passwordEncoder
    ) {
        repository = accountRepository;
        encoder = passwordEncoder;
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
                encoder.encode(password),
                nowUtc,
                nowUtc
            ));

            return account.id() != 0;
        } catch (Exception _) {}
        return false;
    }
}