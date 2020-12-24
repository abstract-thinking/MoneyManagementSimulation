package com.example.risk.boundary.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@AllArgsConstructor
public class BuyRecommendation {

    String wkn;
    String name;
    BigDecimal price;
    double vola30Day;
    double rsl;
    String exchange;
    double exchangeRsl;
    BigDecimal notionalSalesPrice;
}
