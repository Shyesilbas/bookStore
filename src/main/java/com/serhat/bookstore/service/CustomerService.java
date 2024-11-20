package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.dto.CustomerRequest;
import com.serhat.bookstore.dto.CustomerResponse;
import com.serhat.bookstore.exception.EmailAlreadyInUseException;
import com.serhat.bookstore.exception.UsernameAlreadyInUseException;
import com.serhat.bookstore.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakUserService keycloakUserService;

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request){

        if (customerRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException("Email is already in use");
        }

        if (customerRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyInUseException("Username is already in use");
        }

           Customer customer = Customer.builder()
                   .username(request.username())
                   .password(passwordEncoder.encode(request.password()))
                   .email(request.email())
                   .phone(request.phone())
                   .build();

           customerRepository.save(customer);
           keycloakUserService.createKeycloakUser(customer);



           return new CustomerResponse(
                   "Account Created Successfully",
                   customer.getCustomerId(),
                   customer.getUsername()
           );
    }
}
