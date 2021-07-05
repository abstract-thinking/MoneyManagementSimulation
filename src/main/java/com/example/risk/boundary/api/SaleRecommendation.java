package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Value
@Builder
public class SaleRecommendation {

    String symbol;

    String name;
    double rsl;

    BigDecimal price;
    BigDecimal notionalSalesPrice;

    boolean shouldSellByRslComparison;
    boolean shouldSellByStopPrice;
}
