package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CurrentData {
    String wkn;
    String name;
    BigDecimal purchasePrice;
    BigDecimal currentPrice;
    BigDecimal currentStopPrice;
    BigDecimal initialStopPrice;
    double rsl;
}
