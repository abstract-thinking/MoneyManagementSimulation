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
    double exchangeRsl;

    int quantity;
    BigDecimal transactionCosts;
    BigDecimal positionRisk;
}

