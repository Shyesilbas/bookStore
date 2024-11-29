package com.serhat.bookstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    @Column(name = "phone",unique = true)
    private String phone;
    @Enumerated(EnumType.STRING)
    private MemberShipStatus memberShipStatus;
    @Enumerated(EnumType.STRING)
    private IsCustomerVerified isUserVerified;
    @Column(name = "total_reserved_book")
    private int totalReservedBook;
    private int totalBoughtBook;
    private int active_reservations;
    private BigDecimal total_saved;
    @Column(name = "total_comments")
    private int totalComments;
    @Column(name = "total_likes")
    private int totalLikes;
    @Column(name = "total_dislikes")
    private int totalDislikes;
    @Column(name = "total_reposts")
    private int totalReposts;
    @Column(name = "total_likes_received")
    private int totalLikesReceived;
    @Column(name = "total_dislikes_received")
    private int totalDislikesReceived;
    @Column(name = "total_reposts_received")
    private int totalRepostsReceived;



}
