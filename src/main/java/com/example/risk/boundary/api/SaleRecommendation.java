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

    String wkn;

    String name;
    Double rsl;

    BigDecimal price;
    BigDecimal initialNotionalSalesPrice;

    boolean shouldSellByRslComparison;
    boolean shouldSellByFallingBelowTheLimit;

    public boolean shouldSell() {
        return shouldSellByFallingBelowTheLimit || shouldSellByRslComparison;
    }
}
