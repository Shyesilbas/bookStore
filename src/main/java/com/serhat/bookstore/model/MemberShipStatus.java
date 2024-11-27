package com.serhat.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;

@AllArgsConstructor
@Getter
public enum MemberShipStatus {
    BASIC (BigDecimal.ZERO),
    PREMIUM(new BigDecimal("12.50")),
    VIP(new BigDecimal("25.99"));

    private final BigDecimal fee;
}
