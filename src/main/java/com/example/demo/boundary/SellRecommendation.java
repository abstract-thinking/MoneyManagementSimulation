package com.example.demo.boundary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode
public class SellRecommendation {

    String company;
    Double companyRsl;
    String exchange;
    Double exchangeRsl;

    @JsonProperty
    public boolean shouldSell() {
        return exchangeRsl > companyRsl;
    }

    public String toString() {
        return String.format("Recommendation([" +
                "Company: %s, " +
                "Company RSL: %f, " +
                "Exchange: %s, " +
                "Exchange RSL: %f, " +
                "Should sell: %b])", company, companyRsl, exchange, exchangeRsl, shouldSell());
    }
}
