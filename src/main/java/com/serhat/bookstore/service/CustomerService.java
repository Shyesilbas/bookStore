package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.Repository.ReservedBookRepository;
import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.exception.*;
import com.serhat.bookstore.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ReservedBookRepository reservedBookRepository;
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

        if(customerRepository.existsByPhone(request.phone())){
            throw new PhoneAlreadyInUseException("Account Found related to Phone Number");
        }

           Customer customer = Customer.builder()
                   .username(request.username())
                   .password(request.password())
                   .memberShipStatus(MemberShipStatus.BASIC)
                   .isUserVerified(IsCustomerVerified.VERIFIED)
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

    @Transactional
    public DeleteCustomerResponse deleteCustomer(Principal principal){
        String username = principal.getName();
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not found : "+username));
        if(customer.getTotalReservedBook()>0){
            throw new AccountCannotBeDeletedException("You Have Reserved Books , after Bringing back , you can delete your account.");
        }
        customerRepository.delete(customer);
        keycloakUserService.deleteKeycloakUser(customer);
        return new DeleteCustomerResponse(
                "Successfully Deleted",
                customer.getCustomerId(),
                customer.getUsername(),
                customer.getEmail()
        );
    }

    @Transactional
    public UpdatePhoneNumberResponse updatePhoneNumber(UpdatePhoneNumberRequest request , Principal principal){
        String username = principal.getName();
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not found : "+username));


        if(request.newPhone().isBlank()){
            throw new UpdateErrorException("Update Request Cannot be Null!");
        }
        if (customerRepository.existsByPhone(request.newPhone())){
            throw new UpdateErrorException("Phone Number is already in use");
        }
        customer.setPhone(request.newPhone());
        customerRepository.save(customer);

        return new UpdatePhoneNumberResponse(
                "Phone Number Updated Successfully",
                customer.getUsername(),
                request.newPhone()
        );
    }

    @Transactional
    public UpdateEmailResponse updateEmail(UpdateEmailRequest request , Principal principal){
        String username = principal.getName();
        log.info("Fetched Username : "+username);
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not found : "+username));

        if(request.newEmail().isBlank()){
            throw new UpdateErrorException("Update Request Cannot be Null!");
        }
        if(customerRepository.existsByEmail(request.newEmail())){
            throw new UpdateErrorException("Email is already in use : "+request.newEmail());
        }
        customer.setEmail(request.newEmail());
        log.info(customer + " Email Updated Successfully");
        customerRepository.save(customer);

        return new UpdateEmailResponse(
                "Email Updated Successfully",
                customer.getUsername(),
                request.newEmail()
        );
    }
    public List<ActiveReservationsResponse> activeReservationsList (Principal principal){
        String username = principal.getName().toLowerCase();
        Customer customer = customerRepository.findByUsername(username)
                        .orElseThrow(()-> new CustomerNotFoundException("Customer Not Found"));

        log.info(" Customer {} displayed their active reservations.", username);
        List<ReservedBook> activeReservations = reservedBookRepository.findByCustomer_CustomerIdAndReservationStatus(customer.getCustomerId(), ReservationStatus.ON_RESERVATION);
        if(activeReservations.isEmpty()){
            throw new NoActiveReservationsException("No Active Reservation Found");
        }
        return reservedBookRepository.findByCustomer_CustomerIdAndReservationStatus(customer.getCustomerId(), ReservationStatus.ON_RESERVATION)
                .stream()
                .map(reservedBook -> new ActiveReservationsResponse(
                        reservedBook.getCustomer().getUsername(),
                        reservedBook.getReservedBookId(),
                        reservedBook.getBook().getIsbn(),
                        reservedBook.getBook().getTitle(),
                        reservedBook.getReservationDate(),
                        reservedBook.getReservedUntil(),
                        null,
                        reservedBook.getReservationFee(),
                        reservedBook.getReservationStatus()
                ))
                .toList();
    }
    public List<ExpiredReservationsResponse> expiredReservationsList (Principal principal){
        String username = principal.getName().toLowerCase();
        Customer customer = customerRepository.findByUsername(username)
                        .orElseThrow(()-> new CustomerNotFoundException("Customer not found for "+username));
        log.info("{} displayed the active reservations.", username);
        List<ReservedBook> expiredReservations = reservedBookRepository.findByCustomer_CustomerIdAndReservationStatus(customer.getCustomerId(), ReservationStatus.BROUGHT_BACK);
        if(expiredReservations.isEmpty()){
            throw new NoExpiredReservationsException("No Expired Reservation Found");
        }
        return reservedBookRepository.findByCustomer_CustomerIdAndReservationStatus(customer.getCustomerId(), ReservationStatus.BROUGHT_BACK)
                .stream()
                .map(reservedBook -> new ExpiredReservationsResponse(
                        reservedBook.getCustomer().getUsername(),
                        reservedBook.getReservedBookId(),
                        reservedBook.getBook().getIsbn(),
                        reservedBook.getBook().getTitle(),
                        reservedBook.getReservationDate(),
                        reservedBook.getReservedUntil(),
                        null,
                        reservedBook.getReservationFee(),
                        reservedBook.getReservationStatus()
                ))
                .toList();
    }

    @Transactional
    public PayReservationFeeResponse payReservationFee (PayReservationFeeRequest request , Principal principal){
        String username = principal.getName().toLowerCase();
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found for username : "+username));
        ReservedBook reservedBook = reservedBookRepository.findById(request.reservationId())
                .orElseThrow(()-> new ReservationNotFoundException("Reservation Not Found for id : "+request.reservationId()));

        reservedBook.setIsFeePayed(IsFeePayed.PAYED);
        reservedBookRepository.save(reservedBook);
        return new PayReservationFeeResponse(
                "Reservation Fee payed Successfully",
                customer.getUsername(),
                request.reservationId(),
                reservedBook.getReservationFee(),
                LocalDateTime.now()
        );
    }

}
