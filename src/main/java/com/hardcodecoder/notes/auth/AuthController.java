package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.auth.model.AuthResult;
import com.hardcodecoder.notes.auth.model.SignupRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(@NonNull AuthService authService) {
        service = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResult> register(@RequestBody SignupRequest request) {
        try {
            var response = service.processSignUpRequest(request);
            return switch (response) {
                case AuthResult.Success success -> ResponseEntity.ok(success);
                case AuthResult.Error error -> ResponseEntity.badRequest().body(error);
            };
        } catch (Exception _) {
            return ResponseEntity
                .internalServerError()
                .build();
        }
    }

    @GetMapping("/token")
    public ResponseEntity<AuthResult> obtainToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeaderToken) {
        try {
            var response = service.processLoginRequest(authHeaderToken);
            return switch (response) {
                case AuthResult.Success success -> ResponseEntity.ok(success);
                case AuthResult.Error error -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            };
        } catch (Exception _) {
            return ResponseEntity
                .internalServerError()
                .build();
        }
    }
}