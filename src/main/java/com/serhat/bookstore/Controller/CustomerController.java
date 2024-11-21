package com.serhat.bookstore.Controller;

import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.model.Customer;
import com.serhat.bookstore.service.CustomerService;
import com.serhat.bookstore.service.UpdatePhoneNumberRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/api/customer")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request){
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<DeleteCustomerResponse> deleteCustomer(Principal principal){
        return ResponseEntity.ok(customerService.deleteCustomer(principal));
    }

    @PutMapping("/updatePhoneNumber")
    public ResponseEntity<UpdatePhoneNumberResponse> updatePhoneNumber(@RequestBody UpdatePhoneNumberRequest request , Principal principal){
        return ResponseEntity.ok(customerService.updatePhoneNumber(request,principal));
    }
    @PutMapping("/updateEmail")
    public ResponseEntity<UpdateEmailResponse> updateEmail(@RequestBody UpdateEmailRequest request , Principal principal){
        return ResponseEntity.ok(customerService.updateEmail(request,principal));
    }

}
