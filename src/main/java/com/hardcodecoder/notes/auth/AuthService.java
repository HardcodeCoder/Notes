package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.AccountService;
import com.hardcodecoder.notes.auth.model.AuthResult;
import com.hardcodecoder.notes.auth.model.SignupRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AccountService accountService;
    private final TokenService tokenService;

    public AuthService(
        @NonNull AccountService accountService,
        @NonNull TokenService tokenService
    ) {
        this.accountService = accountService;
        this.tokenService = tokenService;
    }

    @NonNull
    public AuthResult processSignUpRequest(@NonNull SignupRequest request) {
        if (null == request.email() || null == request.password())
            return new AuthResult.Error("Invalid Request");

        if (accountService.checkAccountExist(request.email()))
            return new AuthResult.Error("Email already registered");

        var optionalAccount = accountService.create(request.name(), request.email(), request.password());
        if (optionalAccount.isEmpty())
            return new AuthResult.Error("Failed to signup, invalid request");

        var optionalToken = tokenService.createToken(optionalAccount.get());

        if (optionalToken.isPresent()) {
            var token = optionalToken.get();
            return new AuthResult.Success(
                token.accessToken(),
                token.refreshToken(),
                tokenService.expiryTime(token)
            );
        }

        return new AuthResult.Error("Server encounter an issue");
    }

    @NonNull
    public AuthResult refreshAccessToken(@NonNull String authToken, long accountId) {
        if (authToken.length() <= 7 || !authToken.startsWith("Bearer") || accountId < 1)
            return new AuthResult.Error("Invalid request");

        var refreshToken = authToken.substring(7);
        if (!tokenService.isRefreshToken(refreshToken))
            return new AuthResult.Error("Invalid refresh token");

        var optionalAccount = accountService.findById(accountId);
        if (optionalAccount.isEmpty())
            return new AuthResult.Error("Invalid account id");

        var optionalToken = tokenService.updateToken(optionalAccount.get(), refreshToken);

        if (optionalToken.isPresent()) {
            var token = optionalToken.get();
            return new AuthResult.Success(
                token.accessToken(),
                token.refreshToken(),
                tokenService.expiryTime(token)
            );
        }

        return new AuthResult.Error("Failed to authenticate or invalid token");
    }
}