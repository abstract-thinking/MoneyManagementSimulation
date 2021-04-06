package com.example.risk.control.management.caclulate;

import com.example.risk.converter.ExchangeData;

import java.math.BigDecimal;
import java.util.List;

public abstract class Calculator {

    protected static final String EXCHANGE_NAME = "NASDAQ 100";

    protected static final BigDecimal EXCHANGE_TRANSACTION_COSTS = BigDecimal.valueOf(35.50);

    protected final List<ExchangeData> exchangeData;

    protected final double exchangeRsl;

    public Calculator(List<ExchangeData> exchangeData) {
        this.exchangeData = exchangeData;

        this.exchangeRsl = findExchangeRsl();
    }

    private double findExchangeRsl() {
        return exchangeData.stream()
                .filter(result -> result.getName().equals(EXCHANGE_NAME))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

}
