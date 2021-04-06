package com.example.risk.converter;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ExchangeData {
    String wkn;
    String name;
    BigDecimal price;
    double vola30Day;
    double rsl;
}
