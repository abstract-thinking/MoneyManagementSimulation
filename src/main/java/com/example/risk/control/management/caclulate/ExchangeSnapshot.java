package com.example.risk.control.management.caclulate;

import com.example.risk.converter.ExchangeData;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class ExchangeSnapshot {

    String exchangeName;

    BigDecimal exchangeTransactionCosts;

    List<ExchangeData> exchangeData;

    double exchangeRsl;

    public ExchangeSnapshot(List<ExchangeData> exchangeData) {
        this.exchangeName = "NASDAQ 100";
        this.exchangeTransactionCosts = BigDecimal.valueOf(35.50);
        this.exchangeData = exchangeData;
        this.exchangeRsl = findExchangeRsl(exchangeData);
    }

    private double findExchangeRsl(List<ExchangeData> exchangeData) {
        return exchangeData.stream()
                .filter(result -> result.getName().equals(exchangeName))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

    public ExchangeData findExchangeData(String wkn) {
        return exchangeData.stream()
                .filter(entry -> entry.getWkn().equalsIgnoreCase(wkn))
                .findFirst()
                .orElseThrow();
    }

}
