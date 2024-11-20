package com.serhat.bookstore.component;

import com.serhat.bookstore.service.KeycloakUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeycloakUserLoader implements CommandLineRunner {
    private final KeycloakUserService keycloakUserService;

    @Override
    public void run(String... args) throws Exception {
        keycloakUserService.addCustomersToKeycloak();
    }
}
