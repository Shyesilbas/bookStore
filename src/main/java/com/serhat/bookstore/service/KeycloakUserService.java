package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.model.Customer;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {
    private final CustomerRepository customerRepository;
    private final Keycloak keycloak;

    @Transactional
    public void addCustomersToKeycloak(){
        List<Customer> customers = customerRepository.findAll();
        customers.forEach(customer -> {

            UserRepresentation keycloakUser = new UserRepresentation();
            keycloakUser.setUsername(customer.getUsername());
            keycloakUser.setEnabled(true);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(String.valueOf(customer.getPassword()));

            keycloakUser.setCredentials(Collections.singletonList(credential));

            keycloak.realm("bookStore")
                    .users()
                    .create(keycloakUser);
        });

    }

}
