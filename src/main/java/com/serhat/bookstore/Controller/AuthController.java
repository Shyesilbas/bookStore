package com.serhat.bookstore.Controller;

import com.serhat.bookstore.service.KeycloakTokenService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final KeycloakTokenService keycloakTokenService;

    @Getter
    @Setter
    public static class LoginRequest {
        private String username;
        private String password;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = keycloakTokenService.obtainToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            return ResponseEntity.ok(token);
        } catch (KeycloakTokenService.TokenRetrievalException e) {
            return ResponseEntity.status(401).body("Authentication Failed: " + e.getMessage());
        }
    }
}
