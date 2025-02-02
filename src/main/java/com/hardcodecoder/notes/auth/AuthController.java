package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.auth.model.AuthResponse;
import com.hardcodecoder.notes.auth.model.SignupRequest;
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
    public ResponseEntity<AuthResponse> register(@RequestBody SignupRequest request) {
        try {
            var response = service.processSignUpRequest(request);
            return ResponseEntity
                .status(response.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
        } catch (Exception _) {
            return ResponseEntity
                .internalServerError()
                .body(new AuthResponse("Failed to process request", false));
        }
    }

    @GetMapping("/token")
    public ResponseEntity<?> obtainToken() {
        return ResponseEntity.ok("Token");
    }
}