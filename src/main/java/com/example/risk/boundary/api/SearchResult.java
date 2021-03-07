package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class SearchResult {

    Long id;

    String wkn;
    String name;
    int quantity;
    BigDecimal purchasePrice;
    BigDecimal transactionCosts;
    BigDecimal investment;
    BigDecimal notionalSalesPrice;
    BigDecimal notionalRevenue;
    BigDecimal positionRisk;
}
