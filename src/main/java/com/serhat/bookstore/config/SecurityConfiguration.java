package com.serhat.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return   httpSecurity.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/customer/create").permitAll()
                        .requestMatchers("/api/book/books/genre/{genre}").permitAll()
                        .requestMatchers("/api/book/byAuthor").permitAll()
                        .requestMatchers("/api/book/byTitle").permitAll()
                        .requestMatchers("api/book/details").permitAll()
                        .requestMatchers("/api/book/byYear").permitAll()
                        .requestMatchers("/api/book/byRateRange/{minRange}/{maxRange}").permitAll()
                        .requestMatchers("/api/buy/mostSellers","api/buy/mostSellersByGenre","/api/buy/mostSellersOfAuthor").permitAll()
                        .requestMatchers("api/book/updatePrice").hasRole("ADMIN")
                        .requestMatchers("/api/book/delete/{isbn}").hasRole("ADMIN")
                        .requestMatchers("/api/book/addBook").hasRole("ADMIN")
                        .requestMatchers("/api/book/updateStock").hasRole("ADMIN")
                        .requestMatchers("/api/reservation/listActiveReservations").hasRole("ADMIN")
                        .requestMatchers("/api/reservation/listExpiredReservations").hasRole("ADMIN")
                        .requestMatchers("/api/reservation/listLateReservations").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf(CsrfConfigurer::disable)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                        .permitAll()
                        .addLogoutHandler((request, response, authentication) -> {
                            String keycloakLogoutUrl = "http://localhost:8080/realms/bookStore/protocol/openid-connect/logout";
                            try {
                                response.sendRedirect(keycloakLogoutUrl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                )
                .oauth2Login(o -> o
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> authorities = new ArrayList<>();


            if (jwt.getClaimAsMap("resource_access") != null) {
                Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
                if (resourceAccess.get("bookStore") != null) {
                    Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get("bookStore");
                    List<String> roles = (List<String>) clientRoles.get("roles");

                    authorities.addAll(
                            roles.stream()
                                    .map(role -> "ROLE_" + role)
                                    .collect(Collectors.toList())
                    );
                }
            }

            return authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });

        jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");

        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
