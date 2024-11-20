package com.serhat.bookstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SoldBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long soldBookId;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer buyer;
    private String isbn;
    private LocalDateTime saleDate;
    private BigDecimal salePrice;
    private String title;

}
