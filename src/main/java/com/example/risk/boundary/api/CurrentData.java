package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CurrentData {
    String wkn;
    String name;
    BigDecimal price;
    BigDecimal stopPrice;
    double rsl;
}
