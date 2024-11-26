package com.serhat.bookstore.dto;

public record PayReservationFeeRequest(
        Long customerId,
        Long reservationId
) {
}
