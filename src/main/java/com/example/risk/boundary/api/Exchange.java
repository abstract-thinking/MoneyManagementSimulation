package com.example.risk.boundary.api;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Exchange {

    final String name;
    final String symbol;
    final BigDecimal transactionCosts;
    double rsl;

    public Exchange(String name, String symbol, BigDecimal transactionCosts) {
        this.name = name;
        this.symbol = symbol;
        this.transactionCosts = transactionCosts;
    }
}
