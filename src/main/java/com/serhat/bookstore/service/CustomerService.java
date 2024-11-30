package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.*;
import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.exception.*;
import com.serhat.bookstore.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ReservedBookRepository reservedBookRepository;
    private final KeycloakUserService keycloakUserService;
    private final SoldBookRepository soldBookRepository;
    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

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
                   .memberShipStatus(request.phone().isBlank() ? MemberShipStatus.BASIC : request.memberShipStatus())
                   .isUserVerified(request.phone().isBlank() ? IsCustomerVerified.UNVERIFIED : IsCustomerVerified.VERIFIED)
                   .email(request.email())
                   .phone(request.phone())
                   .total_saved(BigDecimal.ZERO)
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
    public DeleteCustomerResponse deleteCustomer(DeleteCustomerRequest request , Principal principal){
        String username = principal.getName();

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not found : "+username));
        if(customer.getActive_reservations()>0){
            throw new AccountCannotBeDeletedException("You have active reservations , after bringing back , you can delete your account.");
        }
        if(!request.username().equals(username) || !request.password().equals(customer.getPassword())){
            throw new InvalidCredentialsException("Check your username again!");
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
    public UpdateMembershipStatusResponse updateMemberShip (UpdateMemberShipRequest request , Principal principal){
        String username = principal.getName().toLowerCase();
        log.info(username+" Updating the membership.");
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not found : "+username));
        if (customer.getIsUserVerified().equals(IsCustomerVerified.UNVERIFIED)){
            throw new UnverifiedAccountException("You have to verify your account to update your membership status");
        }
        if (request.status() == null) {
            throw new UnknownPlanException("Membership status cannot be null");
        }
        if(customer.getMemberShipStatus().equals(request.status())){
            throw new SamePlanForUpdateRequestException("Your plan is already "+request.status());
        }

        BigDecimal fee = request.status().getFee();

        MemberShipStatus currentPlan = customer.getMemberShipStatus();
        customer.setMemberShipStatus(request.status());
        customerRepository.save(customer);

        return new UpdateMembershipStatusResponse(
                customer.getUsername(),
                currentPlan,
                request.status(),
                fee
        );
    }

    @Transactional
    public VerifyCustomerResponse verifyCustomer (VerificationRequest request , Principal principal){
        String username = principal.getName().toLowerCase();
        log.info(username+" verifying themself.");
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not found : "+username));
        if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            throw new CustomerAlreadyVerifiedException("You already verified your account by entering phone number.");
        }
        customer.setPhone(request.phone());
        customer.setIsUserVerified(IsCustomerVerified.VERIFIED);
        customer.setMemberShipStatus(MemberShipStatus.BASIC);
        customerRepository.save(customer);

        return new VerifyCustomerResponse(
                "Thank you for verifying your account! Your membership plan has been set to BASIC automatically. You can change it.",
                customer.getUsername(),
                request.phone()
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

    @Transactional
    public List<SoldBookResponse> listPurchaseHistory(Principal principal){
        String username = principal.getName();
        log.info(username + " is listing their Purchase history.. ");
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not Found : "+username));
        List<SoldBook> soldBooks = soldBookRepository.findByBuyer_CustomerId(customer.getCustomerId());
        if(soldBooks.isEmpty()){
            throw new NoBooksSoldException(" You do not purchase any book yet.");
        }
        soldBooks.sort(Comparator.comparing(SoldBook::getSaleDate).reversed());
        return soldBooks
                .stream()
                .map(soldBook -> new SoldBookResponse(
                        soldBook.getIsbn(),
                        soldBook.getBuyer().getUsername(),
                        soldBook.getTitle(),
                        soldBook.getBook().getGenre(),
                        soldBook.getSoldBookId(),
                        soldBook.getSaleDate(),
                        soldBook.getSalePrice()
                ))
                .toList();
    }

    public List<CommentResponse> showComments (Principal principal){
        String username = principal.getName().toLowerCase();
        log.info(username+" is trying to fetch their comments");
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not Found "+username));
        List<Comment> comments = commentRepository.findByCustomer(customer);

        if(comments.isEmpty()){
            log.warn("No comments found for user: " + username);
            throw new CommentNotFoundException("No comment found for customer : "+username);
        }

        return comments.stream()
                .map(comment -> new CommentResponse(
                        customer.getUsername(),
                        comment.getCommentId(),
                        comment.getBook().getTitle(),
                        comment.getComment(),
                        comment.getLikes(),
                        comment.getDislikes(),
                        comment.getRepost()
                ))
                .toList();
    }

    public List<MostInteractedComments> mostLikedComments (Principal principal){
        String username = principal.getName().toLowerCase();
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found : "+username));
        List<Comment> comments = commentRepository.findByCustomer(customer);
        if(comments.isEmpty()){
            throw new NoCommentFoundForBookException("No comment found for this Book");
        }
        boolean zeroLikes = comments.stream().allMatch(comment -> comment.getLikes() == 0);
        if(zeroLikes){
            throw new NoDislikedCommentFoundException("None of your comments get likes");
        }
        int limit = 3;
        List<Comment> filteredComments = comments.stream()
                .filter(comment -> comment.getLikes()>0)
                .sorted(Comparator.comparing(Comment::getLikes).reversed())
                .limit(limit)
                .toList();

        return filteredComments.stream()
                .map(comment -> new MostInteractedComments(
                        comment.getBook().getTitle(),
                        comment.getComment(),
                        comment.getLikes(),
                        comment.getDislikes(),
                        comment.getRepost()
                ))
                .toList();

    }

    public List<MostInteractedComments> mostDislikedComments (Principal principal){
        String username = principal.getName().toLowerCase();
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found : "+username));
        List<Comment> comments = commentRepository.findByCustomer(customer);
        if(comments.isEmpty()){
            throw new NoCommentFoundForBookException("No comment found for this Book");
        }
        boolean zeroDislikes = comments.stream().allMatch(comment -> comment.getDislikes() == 0);
        if(zeroDislikes){
            throw new NoDislikedCommentFoundException("None of your comments get dislike");
        }
        int limit = 3;
     List<Comment> filteredComments = comments.stream()
             .filter(comment -> comment.getDislikes()>0)
             .sorted(Comparator.comparing(Comment::getDislikes).reversed())
             .limit(limit)
             .toList();

     return filteredComments.stream()
             .map(comment -> new MostInteractedComments(
                     comment.getBook().getTitle(),
                     comment.getComment(),
                     comment.getLikes(),
                     comment.getDislikes(),
                     comment.getRepost()
             ))
             .toList();

    }

}
