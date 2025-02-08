package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.AccountService;
import com.hardcodecoder.notes.auth.model.AuthResponse;
import com.hardcodecoder.notes.auth.model.SignupRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AuthService {

    private final AccountService accountService;

    public AuthService(@NonNull AccountService accountService) {
        this.accountService = accountService;
    }

    @NonNull
    public AuthResponse processSignUpRequest(@NonNull SignupRequest request) {
        var signupSuccess = false;
        if (null != request.email() && null != request.password()) {
            signupSuccess = accountService.create(
                request.name(),
                request.email(),
                request.password()
            );
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

            authenticated = accountService.verifyAccountCredentials(authTokens[0], authTokens[1]);
        }

        return new AuthResponse(authenticated ? "Authenticated" : "Invalid credentials", authenticated);
    }
}