package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.exception.CustomerNotFoundException;
import com.serhat.bookstore.exception.InvalidCredentialsException;
import com.serhat.bookstore.model.Customer;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KeycloakTokenService {

    @Value("${keycloak.token-url}")
    private String tokenUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final CustomerRepository customerRepository;


    public String obtainToken(String username, String password) throws TokenRetrievalException {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + username));

        if (!customer.getPassword().equals(password)){
            throw new InvalidCredentialsException("Invalid Credentials");
        }



        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl("http://localhost:8080")
                    .realm("bookStore")
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(username)
                    .password(password)
                    .grantType(OAuth2Constants.PASSWORD)
                    .build();

            AccessTokenResponse tokenResponse = keycloak.tokenManager().grantToken();
            return tokenResponse.getToken();
        } catch (Exception e) {
            throw new TokenRetrievalException("Failed to retrieve token: " + e.getMessage(), e);
        }
    }
    public static class TokenRetrievalException extends RuntimeException {
        public TokenRetrievalException(String message, Throwable cause) {
            super(message, cause);
        }
    }


}