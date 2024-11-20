package com.serhat.bookstore.Controller;

import com.serhat.bookstore.dto.CustomerRequest;
import com.serhat.bookstore.dto.CustomerResponse;
import com.serhat.bookstore.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/customer")
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request){
        return ResponseEntity.ok(customerService.createCustomer(request));
    }


}
