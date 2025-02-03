package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.AccountService;
import com.hardcodecoder.notes.auth.model.AuthResponse;
import com.hardcodecoder.notes.auth.model.SignupRequest;
import com.hardcodecoder.notes.core.DataValidator;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;

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

    @NonNull
    public AuthResponse processLoginRequest(@NonNull String basicAuthToken) {
        var authenticated = false;
        if (basicAuthToken.length() > 6 && basicAuthToken.startsWith("Basic")) {
            var authToken64 = basicAuthToken.substring(6);
            var authTokens = new String(Base64.getDecoder().decode(authToken64)).split(":");

            if (authTokens.length != 2) {
                return new AuthResponse("Invalid auth token", false);
            }

            if (emailValidator.validate(authTokens[0]) && passwordValidator.validate(authTokens[1])) {
                var optionalAccount = accountService.findByEmail(authTokens[0]);
                if (optionalAccount.isPresent()) {
                    authenticated = passwordEncoder.matches(authTokens[1], optionalAccount.get().password());
                }
            }
        }

        return new AuthResponse(authenticated ? "Authenticated" : "Invalid credentials", authenticated);
    }
}