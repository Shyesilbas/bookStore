package com.serhat.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpiredReservationsResponse (
        String customerUsername,
        Long reservationId,
        String isbn,
        String title,
        LocalDateTime by,
        LocalDateTime until,
        LocalDateTime returnDate,
        BigDecimal reservationFee

){

}
