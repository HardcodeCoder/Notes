package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.AccountService;
import com.hardcodecoder.notes.auth.model.SignupRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        @NonNull AccountService accountService,
        @NonNull PasswordEncoder passwordEncoder
    ) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @NonNull
    public ResponseEntity<?> processSignUpRequest(@NonNull SignupRequest request) {
        if (request.email() == null || request.email().isBlank()) {
            return ResponseEntity.badRequest().body("Invalid Email");
        }
        if (null == request.password() || request.password().isBlank()) {
            return ResponseEntity.badRequest().body("Invalid password");
        }
        var result = accountService.create(
            request.name(),
            request.email(),
            passwordEncoder.encode(request.password())
        );
        return result ?
               ResponseEntity.status(HttpStatus.CREATED).body("Success") :
               ResponseEntity.internalServerError().build();
    }
}