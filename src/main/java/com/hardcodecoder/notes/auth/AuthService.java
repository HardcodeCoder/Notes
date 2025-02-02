package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.AccountService;
import com.hardcodecoder.notes.auth.model.AuthResponse;
import com.hardcodecoder.notes.auth.model.SignupRequest;
import com.hardcodecoder.notes.core.DataValidator;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final DataValidator<String> emailValidator;
    private final DataValidator<String> passwordValidator;

    public AuthService(
        @NonNull AccountService accountService,
        @NonNull PasswordEncoder passwordEncoder,
        @NonNull DataValidator<String> emailValidator,
        @NonNull DataValidator<String> passwordValidator
    ) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
    }

    @NonNull
    public AuthResponse processSignUpRequest(@NonNull SignupRequest request) {
        var signupSuccess = false;
        if (null != request.email() && null != request.password()) {
            if (emailValidator.validate(request.email()) && passwordValidator.validate(request.password())) {
                signupSuccess = accountService.create(
                    request.name(),
                    request.email(),
                    passwordEncoder.encode(request.password())
                );
            }
        }

        return new AuthResponse(signupSuccess ? "Request processed" : "Invalid Request", signupSuccess);
    }
}