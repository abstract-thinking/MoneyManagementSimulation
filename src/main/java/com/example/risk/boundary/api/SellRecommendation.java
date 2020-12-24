package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@ToString
public class SellRecommendation {

    String wkn;
    String company;
    Double companyRsl;
    String exchange;
    Double exchangeRsl;
    BigDecimal price;

    public boolean shouldSell() {
        return exchangeRsl > companyRsl;
    }

}
