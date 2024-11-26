package com.serhat.bookstore.customer;

import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.dto.DeleteCustomerRequest;
import com.serhat.bookstore.dto.DeleteCustomerResponse;
import com.serhat.bookstore.exception.AccountCannotBeDeletedException;
import com.serhat.bookstore.exception.CustomerNotFoundException;
import com.serhat.bookstore.exception.InvalidCredentialsException;
import com.serhat.bookstore.model.Customer;
import com.serhat.bookstore.service.CustomerService;
import com.serhat.bookstore.service.KeycloakUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCustomerTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private KeycloakUserService keycloakUserService;

    @Mock
    private Principal principal;

    @InjectMocks
    private CustomerService customerService;

    private Customer existingCustomer;
    private DeleteCustomerRequest validRequest;

    @BeforeEach
    void setUp() {
        existingCustomer = new Customer();
        existingCustomer.setUsername("testuser");
        existingCustomer.setPassword("password123");
        existingCustomer.setCustomerId(1L);
        existingCustomer.setEmail("test@example.com");
        existingCustomer.setActive_reservations(0);

        validRequest = new DeleteCustomerRequest(
                existingCustomer.getUsername(),
                existingCustomer.getPassword()
        );
    }

    @Test
    void deleteCustomer_Successful() {
        when(principal.getName()).thenReturn("testuser");
        when(customerRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(existingCustomer));

        DeleteCustomerResponse response = customerService.deleteCustomer(validRequest, principal);

        assertNotNull(response);
        assertEquals("Successfully Deleted", response.message());
        assertEquals(existingCustomer.getCustomerId(), response.customerId());
        assertEquals(existingCustomer.getUsername(), response.name());
        assertEquals(existingCustomer.getEmail(), response.email());

        verify(customerRepository).findByUsername("testuser");
        verify(customerRepository).delete(existingCustomer);
        verify(keycloakUserService).deleteKeycloakUser(existingCustomer);
    }

    @Test
    void deleteCustomer_CustomerNotFound() {
        when(principal.getName()).thenReturn("nonexistentuser");
        when(customerRepository.findByUsername("nonexistentuser"))
                .thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () ->
                customerService.deleteCustomer(validRequest, principal)
        );
    }

    @Test
    void deleteCustomer_ActiveReservationsPresent() {
        existingCustomer.setActive_reservations(2);
        when(principal.getName()).thenReturn("testuser");
        when(customerRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(existingCustomer));

        assertThrows(AccountCannotBeDeletedException.class, () ->
                customerService.deleteCustomer(validRequest, principal)
        );
    }

    @Test
    void deleteCustomer_InvalidCredentials() {
        when(principal.getName()).thenReturn("testuser");
        when(customerRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(existingCustomer));

        DeleteCustomerRequest invalidRequest = new DeleteCustomerRequest(
                "wronguser",
                "wrongpassword"
        );

        assertThrows(InvalidCredentialsException.class, () ->
                customerService.deleteCustomer(invalidRequest, principal)
        );
    }
}