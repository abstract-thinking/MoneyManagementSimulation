package com.example.risk.control.management;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.risk.control.management.caclulate.PriceCalculator.calculateNotionalSalesPrice;
import static org.assertj.core.api.Assertions.assertThat;

class PriceCalculatorTest {

    @Test
    public void shouldCalculatePrice() {
        BigDecimal calculateNotionalSalesPrice =
                calculateNotionalSalesPrice(1.14, BigDecimal.valueOf(217.77), 1.12);

        assertThat(calculateNotionalSalesPrice).isEqualTo(BigDecimal.valueOf(213.9494));
    }

}