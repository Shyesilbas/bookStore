package com.serhat.bookstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservedBookId;
    private String title;
    private String isbn;
    private LocalDateTime reservationDate;
    private LocalDateTime reservedUntil;
    private BigDecimal reservationFee;
    @Enumerated(EnumType.STRING)
    @Column(name = "is_fee_payed")
    private IsFeePayed isFeePayed;
    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status")
    private ReservationStatus reservationStatus;
    private LocalDateTime return_date;

    @ManyToOne
    @JoinColumn(name = "customer_id",nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "book_id",nullable = false)
    private Book book;
}
