package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Value
@Builder
@ToString
public class SaleRecommendation {

    Long id;

    String wkn;

    String name;
    double rsl;

    BigDecimal price;
    BigDecimal notionalSalesPrice;

    boolean shouldSellByRslComparison;
    boolean shouldSellByFallingBelowTheLimit;

    public boolean shouldSell() {
        return shouldSellByFallingBelowTheLimit || shouldSellByRslComparison;
    }
}
