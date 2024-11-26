package com.serhat.bookstore.Repository;

import com.serhat.bookstore.dto.ActiveReservationsResponse;
import com.serhat.bookstore.model.ReservationStatus;
import com.serhat.bookstore.model.ReservedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservedBookRepository extends JpaRepository<ReservedBook,Long> {
    Optional<ReservedBook> findByIsbn(String isbn);


    List<ReservedBook> findByReservationStatus(ReservationStatus status);

    List<ReservedBook> findByCustomer_CustomerIdAndReservationStatus(Long customerId , ReservationStatus reservationStatus);
}
