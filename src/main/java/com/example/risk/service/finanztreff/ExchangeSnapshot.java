package com.example.risk.service.finanztreff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@ToString
public class ExchangeSnapshot {

    final String name;

    final BigDecimal transactionCosts;

    double rsl;

    List<Quotes> quotes;

    public ExchangeSnapshot(List<Quotes> quotes) {
        this.name = "NASDAQ 100";
        // Gesamtkosten Kauf + Verkauf: 48,38 EUR (0,03 %)
        // Gesamtkosten Kauf + Verkauf: 40,34 EUR (0,04 %)
        // Gesamtkosten Kauf + Verkauf: 51,82 EUR (0,02 %)
        // Gesamtkosten Kauf + Verkauf: 53,13 EUR (0,02 %)
        // Gesamtkosten Kauf + Verkauf: 43,77 EUR (0,03 %)
        this.transactionCosts = BigDecimal.valueOf(60);
        this.quotes = quotes;
        this.rsl = findExchangeRsl(quotes);
    }

    private double findExchangeRsl(List<Quotes> exchangeData) {
        return exchangeData.stream()
                .filter(result -> result.getName().equals(name))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

    @Data
    @Builder
    @ToString
    public static class Quotes {
        String wkn;
        String name;
        BigDecimal price;
        double rsl;
    }

    @Data
    @Builder
    @ToString
    public static class Result {
        String wkn;
        String name;
        BigDecimal stopPrice;
    }
}
