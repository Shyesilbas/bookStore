package com.serhat.bookstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    @Column(name = "username",nullable = false)
    private String username;
    @Column(name = "password",nullable = false)
    private String password;
    @Column(name = "email",nullable = false,unique = true)
    private String email;
    @Column(name = "phone",nullable = false,unique = true)
    private String phone;
    @Enumerated(EnumType.STRING)
    private MemberShipStatus memberShipStatus;
    @Enumerated(EnumType.STRING)
    private IsCustomerVerified isUserVerified;
    @Column(name = "total_reserved_book")
    private int totalReservedBook;
    private int totalBoughtBook;


}
