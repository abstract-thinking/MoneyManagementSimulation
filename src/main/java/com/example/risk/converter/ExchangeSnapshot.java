package com.example.risk.converter;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class ExchangeSnapshot {

    String name;

    BigDecimal transactionCosts;

    List<ExchangeData> data;

    double rsl;

    public ExchangeSnapshot(List<ExchangeData> data) {
        this.name = "NASDAQ 100";
        this.transactionCosts = BigDecimal.valueOf(36);
        this.data = data;
        this.rsl = findExchangeRsl(data);
    }

    private double findExchangeRsl(List<ExchangeData> exchangeData) {
        return exchangeData.stream()
                .filter(result -> result.getName().equals(name))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

    @Value
    @Builder
    public static class ExchangeData {
        String wkn;
        String name;
        BigDecimal price;
        double vola30Day;
        double rsl;
    }
}
