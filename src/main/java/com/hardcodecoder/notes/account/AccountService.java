package com.hardcodecoder.notes.account;

import com.hardcodecoder.notes.account.model.Account;
import com.hardcodecoder.notes.core.DataValidator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataValidator<String> emailValidator;
    private final DataValidator<String> passwordValidator;

    public AccountService(
        @NonNull AccountRepository accountRepository,
        @NonNull PasswordEncoder passwordEncoder,
        @NonNull DataValidator<String> emailValidator,
        @NonNull DataValidator<String> passwordValidator
    ) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
    }

    public boolean create(@Nullable String name, @NonNull String email, @NonNull String password) {
        if (emailValidator.validate(email) && passwordValidator.validate(password)) {
            var now = OffsetDateTime.now(ZoneOffset.UTC);
            var account = accountRepository.save(new Account(
                0,
                name,
                email,
                passwordEncoder.encode(password),
                now,
                now
            ));
            return account.id() != 0;
        }
        return false;
    }

    public boolean verifyAccountCredentials(@NonNull String email, @NonNull String password) {
        if (emailValidator.validate(email) && passwordValidator.validate(password)) {
            var account = accountRepository.findByEmail(email);
            return account.isPresent() &&
                   passwordEncoder.matches(password, account.get().password());
        }
        return false;
    }

    public boolean checkAccountExist(@NonNull String email) {
        return emailValidator.validate(email) &&
               accountRepository.findByEmail(email).isPresent();
    }
}