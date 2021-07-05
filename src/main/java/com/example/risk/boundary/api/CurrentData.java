package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CurrentData {
    String symbol;
    String name;
    BigDecimal purchasePrice;
    BigDecimal currentPrice;
    BigDecimal initialStopPrice;
    BigDecimal currentStopPrice;
    double rsl;
}
