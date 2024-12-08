package com.serhat.bookstore.Controller;

import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.service.CustomerService;
import com.serhat.bookstore.dto.UpdatePhoneNumberRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api/customer")
@Controller
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping("/create")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request){
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<DeleteCustomerResponse> deleteCustomer(@RequestBody DeleteCustomerRequest request  ,Principal principal){
        return ResponseEntity.ok(customerService.deleteCustomer(request,principal));
    }

    @PutMapping("/updatePhoneNumber")
    public ResponseEntity<UpdatePhoneNumberResponse> updatePhoneNumber(@RequestBody UpdatePhoneNumberRequest request , Principal principal){
        return ResponseEntity.ok(customerService.updatePhoneNumber(request,principal));
    }
    @PutMapping("/updateEmail")
    public ResponseEntity<UpdateEmailResponse> updateEmail(@RequestBody UpdateEmailRequest request , Principal principal){
        return ResponseEntity.ok(customerService.updateEmail(request,principal));
    }

    @GetMapping("/listActiveReservations")
    public ResponseEntity<List<ActiveReservationsResponse>> listActiveReservations (Principal principal){
        return ResponseEntity.ok(customerService.activeReservationsList(principal));
    }
    @GetMapping("/listExpiredReservations")
    public ResponseEntity<List<ExpiredReservationsResponse>> listExpiredReservations (Principal principal){
        return ResponseEntity.ok(customerService.expiredReservationsList(principal));
    }

    @PostMapping("/payReservationFee")
    public ResponseEntity<PayReservationFeeResponse> payReservationFee (@RequestBody PayReservationFeeRequest request , Principal principal){
        return ResponseEntity.ok(customerService.payReservationFee(request,principal));
    }

    @GetMapping("/purchaseHistory")
    public String purchaseHistory(Model model, Principal principal) {
        List<SoldBookResponse> history = customerService.listPurchaseHistory(principal);
        model.addAttribute("purchaseHistory", history);
        return "purchase-history";
    }


    @PutMapping("/verifyCustomer")
    public ResponseEntity<VerifyCustomerResponse> verifyCustomer (@RequestBody VerificationRequest request , Principal principal){
        return ResponseEntity.ok(customerService.verifyCustomer(request, principal));
    }

    @PutMapping("/updatePlan")
    public ResponseEntity<UpdateMembershipStatusResponse> updateMembership(@RequestBody UpdateMemberShipRequest request ,Principal principal){
        return ResponseEntity.ok(customerService.updateMemberShip(request, principal));
    }

    @GetMapping("/getComments")
    public ResponseEntity<List<CommentResponse>> getComments (Principal principal){
        return ResponseEntity.ok(customerService.showComments(principal));
    }

    @GetMapping("/mostLikedComments")
    public ResponseEntity<List<MostInteractedComments>> mostLikedComments(Principal principal){
        return ResponseEntity.ok(customerService.mostLikedComments(principal));
    }
    @GetMapping("/mostDislikedComments")
    public ResponseEntity<List<MostInteractedComments>> mostDislikedComments(Principal principal){
        return ResponseEntity.ok(customerService.mostDislikedComments(principal));
    }

}
