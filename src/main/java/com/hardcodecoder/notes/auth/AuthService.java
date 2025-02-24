package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.AccountService;
import com.hardcodecoder.notes.auth.model.AuthResult;
import com.hardcodecoder.notes.auth.model.SignupRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AuthService {

    private final AccountService accountService;
    private final JwtService jwtService;

    public AuthService(
        @NonNull AccountService accountService,
        @NonNull JwtService jwtService
    ) {
        this.accountService = accountService;
        this.jwtService = jwtService;
    }

    @NonNull
    public AuthResult processSignUpRequest(@NonNull SignupRequest request) {
        if (null != request.email() && null != request.password()) {

            if (accountService.checkAccountExist(request.email())) {
                return new AuthResult.Error("Account already exists");
            }

            var account = accountService.create(
                request.name(),
                request.email(),
                request.password()
            );

            if (account.isPresent()) {
                var token = jwtService.generateToken(request.email());
                return new AuthResult.Success(token, jwtService.extractExpiration(token));
            }
        }

        return new AuthResult.Error("Invalid Request");
    }

    @NonNull
    public AuthResult processLoginRequest(@NonNull String basicAuthToken) {
        var authenticated = false;
        if (basicAuthToken.length() > 6 && basicAuthToken.startsWith("Basic")) {
            var authToken64 = basicAuthToken.substring(6);
            var authTokens = new String(Base64.getDecoder().decode(authToken64)).split(":");

            if (authTokens.length != 2) {
                return new AuthResult.Error("Invalid auth token");
            }

            authenticated = accountService.verifyAccountCredentials(authTokens[0], authTokens[1]);

            if (authenticated) {
                var token = jwtService.generateToken(authTokens[0]);
                return new AuthResult.Success(token, jwtService.extractExpiration(token));
            }
        }

        return new AuthResult.Error("Invalid credentials");
    }
}