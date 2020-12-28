package com.example.risk.control.management;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PriceCalculator {

    public static BigDecimal calculateNotionalSalesPrice(double rsl, BigDecimal currentPrice, double exchangeRsl) {
        return currentPrice
                .multiply(BigDecimal.valueOf(exchangeRsl))
                .divide(BigDecimal.valueOf(rsl), 4, RoundingMode.DOWN);
    }
}