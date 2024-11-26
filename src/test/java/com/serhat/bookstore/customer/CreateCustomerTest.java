package com.serhat.bookstore.customer;

import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.dto.CustomerRequest;
import com.serhat.bookstore.dto.CustomerResponse;
import com.serhat.bookstore.exception.EmailAlreadyInUseException;
import com.serhat.bookstore.exception.PhoneAlreadyInUseException;
import com.serhat.bookstore.exception.UsernameAlreadyInUseException;
import com.serhat.bookstore.model.Customer;
import com.serhat.bookstore.service.CustomerService;
import com.serhat.bookstore.service.KeycloakUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateCustomerTest {

    @Mock // While testing , creates fake instances
    private CustomerRepository customerRepository;

    @Mock
    private KeycloakUserService keycloakUserService;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequest request;

    @BeforeEach
    void setUp() {
        request = new CustomerRequest("john_doe", "password123", "john@example.com", "1234567890");
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        when(customerRepository.existsByEmail(request.email())).thenReturn(false);
        when(customerRepository.existsByUsername(request.username())).thenReturn(false);
        when(customerRepository.existsByPhone(request.phone())).thenReturn(false);

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response); // assert that response is not null
        assertEquals("Account Created Successfully", response.message());
        assertEquals(request.username(), response.username());

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(keycloakUserService, times(1)).createKeycloakUser(any(Customer.class));
    }

    @Test
    void shouldThrowEmailAlreadyInUseException() {
        when(customerRepository.existsByEmail(request.email())).thenReturn(true);

        EmailAlreadyInUseException exception = assertThrows(
                EmailAlreadyInUseException.class,
                () -> customerService.createCustomer(request)
        );
        assertEquals("Email is already in use", exception.getMessage());

        verify(customerRepository, never()).save(any(Customer.class));
        verify(keycloakUserService, never()).createKeycloakUser(any(Customer.class));
    }

    @Test
    void shouldThrowUsernameAlreadyInUseException() {
        when(customerRepository.existsByUsername(request.username())).thenReturn(true);

        UsernameAlreadyInUseException exception = assertThrows(
                UsernameAlreadyInUseException.class,
                () -> customerService.createCustomer(request)
        );
        assertEquals("Username is already in use", exception.getMessage());

        verify(customerRepository, never()).save(any(Customer.class));
        verify(keycloakUserService, never()).createKeycloakUser(any(Customer.class));
    }

    @Test
    void shouldThrowPhoneAlreadyInUseException() {
        when(customerRepository.existsByPhone(request.phone())).thenReturn(true);

        PhoneAlreadyInUseException exception = assertThrows(
                PhoneAlreadyInUseException.class,
                () -> customerService.createCustomer(request)
        );
        assertEquals("Account Found related to Phone Number", exception.getMessage());

        verify(customerRepository, never()).save(any(Customer.class));
        verify(keycloakUserService, never()).createKeycloakUser(any(Customer.class));
    }
}
