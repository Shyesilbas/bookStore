package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.model.Customer;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserService {
    private final CustomerRepository customerRepository;
    private final Keycloak keycloak;
    private final PasswordEncoder passwordEncoder;

    public void createKeycloakUser(Customer customer) {
        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(customer.getUsername());
            user.setEmail(customer.getEmail());
            user.setEnabled(true);

            CredentialRepresentation credentials = new CredentialRepresentation();
            credentials.setType(CredentialRepresentation.PASSWORD);
            credentials.setValue(customer.getPassword());
            credentials.setTemporary(false);

            user.setCredentials(Collections.singletonList(credentials));


            Response response = keycloak.realm("bookStore")
                    .users()
                    .create(user);

            if (response.getStatus() == 201) {
                log.info("Keycloak User Created: {}", customer.getUsername());
            } else {
                log.error("Keycloak user is not created. Hata kodu: {}", response.getStatus());
            }
        } catch (Exception e) {
            log.error("An error occurred while creating Keycloak User", e);
        }
        };
    }

