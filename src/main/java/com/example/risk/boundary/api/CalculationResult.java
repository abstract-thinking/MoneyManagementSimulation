package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Builder
@Value
public class CalculationResult {
    String wkn;
    String name;

    BigDecimal price;
    BigDecimal notionalSalesPrice;
    double rsl;

    ExchangeResult exchangeResult;

    int quantity;
    BigDecimal transactionCosts;
    BigDecimal positionRisk;
}

