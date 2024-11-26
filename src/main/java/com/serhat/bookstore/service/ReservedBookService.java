package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.BookRepository;
import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.Repository.ReservedBookRepository;
import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.exception.*;
import com.serhat.bookstore.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservedBookService {
    private final ReservedBookRepository reservedBookRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public ReserveBookResponse reserveABook(ReserveBookRequest request, Principal principal) {
        String username = principal.getName().toLowerCase();
        log.info("Customer username (lowercase): " + username);

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found!"));
        Book book = bookRepository.findByIsbn(request.isbn())
                .orElseThrow(() -> new BookNotFoundException("Book not found by ISBN: " + request.isbn()));

        if (!book.getBookStatus().equals(BookStatus.AVAILABLE)) {
            throw new BookIsNotAvailableForReservationException("This book is not available for reservation right now.");
        }
        if(customer.getActive_reservations() == 2){
            throw new ActiveReservationLimitReachedException("You can only have 2 active Reservations");
        }
        ReservedBook existingReservation = reservedBookRepository.findByCustomerAndBookAndReservationStatus(customer, book, ReservationStatus.ON_RESERVATION);
        if (existingReservation != null) {
            throw new BookAlreadyReservedByThisCustomerException("You have active reservation on this book.");
        }

        LocalDateTime reservationDate = LocalDateTime.now();
        log.info("Reservation date (today): " + reservationDate);


        LocalDateTime reservedUntil = request.reservationUntil()
                .atTime(reservationDate.toLocalTime());

        long daysBetween = java.time.Duration.between(reservationDate, reservedUntil).toDays();
        if (daysBetween <= 0) {
            throw new InvalidReservationDateException("Reservation end date must be after today.");
        }

        BigDecimal dailyFee = BigDecimal.ONE;
        BigDecimal reservationFee = book.getPrice()
                .multiply(new BigDecimal("0.10"))
                .add(dailyFee.multiply(BigDecimal.valueOf(daysBetween)));

        ReservedBook reservedBook = ReservedBook.builder()
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .reservationDate(reservationDate)
                .reservedUntil(reservedUntil)
                .reservationFee(reservationFee)
                .isFeePayed(IsFeePayed.ON_RESERVATION)
                .reservationStatus(ReservationStatus.ON_RESERVATION)
                .book(book)
                .customer(customer)
                .build();
        if(reservedBook.getReservationStatus().equals(ReservationStatus.ON_RESERVATION)){
            reservedBook.setReturn_date(null);
        }


        book.setQuantity(book.getQuantity() - 1);
        log.info(String.format("Book quantity updated for '%s'. New quantity: %d", book.getTitle(), book.getQuantity()));

        if (book.getQuantity() == 0) {
            book.setBookStatus(BookStatus.OUT_OF_STOCKS);
        }

        customer.setTotalReservedBook(customer.getTotalReservedBook() + 1);
        customer.setActive_reservations(customer.getActive_reservations()+1);
        log.info(String.format("Customer's total reserved books updated to: %d", customer.getTotalReservedBook()));

        book.setTotal_reserved(book.getTotal_reserved() + 1);
        log.info(String.format("Total reservations for book '%s' updated to: %d", book.getTitle(), book.getTotal_reserved()));

        bookRepository.save(book);
        customerRepository.save(customer);
        reservedBookRepository.save(reservedBook);

        return new ReserveBookResponse(
                "Reservation is successfully placed!",
                reservedBook.getTitle(),
                reservedBook.getIsbn(),
                customer.getUsername(),
                reservedBook.getReservationDate(),
                reservedBook.getReservedUntil(),
                reservationFee
        );
    }



    @Transactional
    public ReturnReservedBookResponse returnReservedBook (ReturnReservedBookRequest request , Principal principal){
        String username = principal.getName().toLowerCase();
        ReservedBook reservedBook = reservedBookRepository.findById(request.reservationId())
                .orElseThrow(()-> new ReservationNotFoundException("No current reservation found on Book : "+ request.isbn()));
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found for username : "+username));
        Book book = bookRepository.findByIsbn(request.isbn())
                .orElseThrow(()-> new BookNotFoundException("Book Not Found : "+request.isbn()));

        if(reservedBook.getIsFeePayed().equals(IsFeePayed.NOT_PAYED) || reservedBook.getIsFeePayed().equals(IsFeePayed.ON_RESERVATION)){
            log.error("Reservation fee not paid for book: {}", reservedBook.getTitle());
            throw new ReservationFeeNotPayedException("You have to pay your reservation Fee. : "+reservedBook.getReservationFee());
        }
        if(!reservedBook.getCustomer().equals(customer)){
            log.error("Customer {} has no reservation on book: {}", username, reservedBook.getTitle());
            throw new CustomerHasNoReservationException(username+" Has no reservation on book : "+reservedBook.getTitle());
        }

        LocalDateTime now = LocalDateTime.now();
        if (reservedBook.getReservedUntil().isBefore(now)) {
            reservedBook.setReservationStatus(ReservationStatus.NOT_BROUGHT_BACK);
            log.warn(String.format("Reservation for book '%s' by user '%s' is overdue.", reservedBook.getBook().getTitle(), username));
        } else {
            reservedBook.setReservationStatus(ReservationStatus.BROUGHT_BACK);
        }

        if(reservedBook.getReservationStatus().equals(ReservationStatus.NOT_BROUGHT_BACK)){
            long daysLate = java.time.Duration.between(reservedBook.getReservedUntil(), now).toDays();
            BigDecimal lateFee = BigDecimal.valueOf(daysLate);
            reservedBook.setReservationFee(reservedBook.getReservationFee().add(lateFee));
            reservedBook.setIsFeePayed(IsFeePayed.NOT_PAYED);
            log.info("Late fee added to reservation for book '{}'. New fee: {}", reservedBook.getBook().getTitle(), lateFee);
        }


        reservedBook.setIsFeePayed(IsFeePayed.PAYED);
        reservedBook.setReservationStatus(ReservationStatus.BROUGHT_BACK);
        book.setQuantity(book.getQuantity()+1);
        if (book.getQuantity() > 0 && book.getBookStatus().equals(BookStatus.OUT_OF_STOCKS)) {
            book.setBookStatus(BookStatus.AVAILABLE);
        }
        book.setTotal_reserved(book.getTotal_reserved()-1);
        customer.setActive_reservations(customer.getActive_reservations()-1);
        reservedBook.setReturn_date(LocalDateTime.now());

        reservedBookRepository.save(reservedBook);
        customerRepository.save(customer);
        bookRepository.save(book);

        return new ReturnReservedBookResponse(
                "Return Of Reservation is Successfully done!",
                customer.getUsername(),
                reservedBook.getIsbn(),
                reservedBook.getReservedBookId(),
                book.getTitle()
        );
    }

    public List<ActiveReservationsResponse> activeReservationsList (Principal principal){
        String username = principal.getName();
        log.info("{} displayed the active reservations.", username);
        List<ReservedBook> activeReservations = reservedBookRepository.findByReservationStatus(ReservationStatus.ON_RESERVATION);
        if(activeReservations.isEmpty()){
            throw new NoActiveReservationsException("No Active Reservation Found");
        }
        return reservedBookRepository.findByReservationStatus(ReservationStatus.ON_RESERVATION)
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
        String username = principal.getName();
        log.info("{} displayed the active reservations.", username);
        List<ReservedBook> expiredReservations = reservedBookRepository.findByReservationStatus(ReservationStatus.BROUGHT_BACK);
        if(expiredReservations.isEmpty()){
            throw new NoExpiredReservationsException("No Expired Reservation Found");
        }
        return reservedBookRepository.findByReservationStatus(ReservationStatus.BROUGHT_BACK)
                .stream()
                .map(reservedBook -> new ExpiredReservationsResponse(
                        reservedBook.getCustomer().getUsername(),
                        reservedBook.getReservedBookId(),
                        reservedBook.getBook().getIsbn(),
                        reservedBook.getBook().getTitle(),
                        reservedBook.getReservationDate(),
                        reservedBook.getReservedUntil(),
                        reservedBook.getReturn_date(),
                        reservedBook.getReservationFee(),
                        reservedBook.getReservationStatus()
                ))
                .toList();
    }

    public List<LateReservationResponse> lateReservationsList(Principal principal){
        String username = principal.getName();
        log.info("{} displayed the active reservations.", username);
        List<ReservedBook> lateReservations = reservedBookRepository.findByReservationStatus(ReservationStatus.NOT_BROUGHT_BACK);
        if(lateReservations.isEmpty()){
            throw new NoExpiredReservationsException("No Late Reservation Found");
        }
        return reservedBookRepository.findByReservationStatus(ReservationStatus.NOT_BROUGHT_BACK)
                .stream()
                .map(reservedBook -> new LateReservationResponse(
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

}



