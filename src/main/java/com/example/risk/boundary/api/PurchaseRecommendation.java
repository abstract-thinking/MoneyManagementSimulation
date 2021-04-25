package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class PurchaseRecommendation {

    String wkn;
    String symbol;
    String name;

    int quantity;

    BigDecimal price;
    BigDecimal notionalSalesPrice;

    double vola30Day;

    double rsl;
}
