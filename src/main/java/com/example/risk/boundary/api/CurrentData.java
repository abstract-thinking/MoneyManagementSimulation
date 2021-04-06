package com.example.risk.boundary.api;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class CurrentData {

    String wkn;
    String name;
    BigDecimal price;
    BigDecimal stopprice;
    double rsl;
}
