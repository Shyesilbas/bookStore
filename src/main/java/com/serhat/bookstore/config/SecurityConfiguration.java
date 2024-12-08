package com.serhat.bookstore.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return   httpSecurity.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/customer/create").permitAll()
                        .requestMatchers("/api/book/books/genre/{genre}").permitAll()
                        .requestMatchers("/api/book/byAuthor").permitAll()
                        .requestMatchers("/api/book/byTitle").permitAll()
                        .requestMatchers("api/book/details").permitAll()
                        .requestMatchers("/api/book/byYear").permitAll()
                        .requestMatchers("/auth/login").permitAll()
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
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2-> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .formLogin(f->f
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .permitAll()
                        .defaultSuccessUrl("/home")
                        .failureUrl("/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("http://localhost:8080/realms/bookStore/protocol/openid-connect/logout?redirect_uri=http://localhost:8254/login")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .oauth2Login(o->o
                        .successHandler(oAuth2LoginSuccessHandler())
                        .loginPage("/login")
                        .defaultSuccessUrl("/home",true))
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2LoginSuccessHandler() {
        return (request, response, authentication) -> {
            if (authentication instanceof OAuth2AuthenticationToken token) {
                String clientRegistrationId = token.getAuthorizedClientRegistrationId();
                OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                        clientRegistrationId, token.getName());

                if (authorizedClient != null) {
                    OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                    log.info("Access Token: " + accessToken.getTokenValue());
                }
            }
            response.sendRedirect("/home");
        };
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
