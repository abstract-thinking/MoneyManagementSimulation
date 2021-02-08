package com.example.risk.control.management.caclulate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PriceCalculator {

    public static BigDecimal calculateNotionalSalesPrice(double rsl, BigDecimal currentPrice, double exchangeRsl) {
        log.info("calculateNotionalSalesPrice: rsl = {}, price = {}, exchangeRsl = {}", rsl, currentPrice, exchangeRsl);
        final BigDecimal notionalSalesPrice = currentPrice
                .multiply(BigDecimal.valueOf(exchangeRsl))
                .divide(BigDecimal.valueOf(rsl), 4, RoundingMode.DOWN);

        log.info("Calculated notional sales price: {}", notionalSalesPrice);
        return notionalSalesPrice;
    }
}
