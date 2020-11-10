package com.example.demo.control.invest;

import java.math.BigDecimal;

import static java.math.RoundingMode.CEILING;

public class PriceCalculator {

    public static BigDecimal calculateNotionalSalesPrice(double rsl, BigDecimal currentPrice, double exchangeRsl) {
        return currentPrice
                .multiply(BigDecimal.valueOf(exchangeRsl))
                .divide(BigDecimal.valueOf(rsl), CEILING);
    }

}
