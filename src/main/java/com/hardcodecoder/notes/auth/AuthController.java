package com.hardcodecoder.notes.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/signup")
    public ResponseEntity<?> register() {
        return ResponseEntity.status(HttpStatus.CREATED).body("Success");
    }

    @GetMapping("/token")
    public ResponseEntity<?> obtainToken() {
        return ResponseEntity.ok("Token");
    }
}