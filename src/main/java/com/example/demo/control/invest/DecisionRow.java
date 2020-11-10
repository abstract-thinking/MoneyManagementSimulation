package com.example.demo.control.invest;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class DecisionRow {
    String wkn;
    String name;
    BigDecimal price;
    double vola30Day;
    double rsl;
}
