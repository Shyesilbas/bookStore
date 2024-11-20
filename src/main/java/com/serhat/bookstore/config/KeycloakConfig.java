package com.serhat.bookstore.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak(){
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("bookStore")
                .clientId("bookStore")
                .username("admin")
                .password("admin")
                .build();
    }


}
