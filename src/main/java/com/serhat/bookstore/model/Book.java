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
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;
    private String isbn;
    private String title;
    private String author;
    @Enumerated(EnumType.STRING)
    private Genre genre;
    private LocalDate releaseDate;
    private double rate;
    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;
    private int quantity;
    private BigDecimal price;
    int total_sold;
    int total_reserved;
    @Column(name = "total_comments")
    private int totalComments;


}
