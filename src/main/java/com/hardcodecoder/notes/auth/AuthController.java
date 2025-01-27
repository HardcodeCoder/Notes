package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.auth.model.SignupRequest;
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
    public ResponseEntity<?> register(@RequestBody SignupRequest request) {
        try {
            return service.processSignUpRequest(request);
        } catch (Exception _) {}
        return ResponseEntity.internalServerError().body("Something went wrong");
    }

    @GetMapping("/token")
    public ResponseEntity<?> obtainToken() {
        return ResponseEntity.ok("Token");
    }
}