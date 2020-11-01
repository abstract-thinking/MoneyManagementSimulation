package com.example.demo.control.management;

import com.example.demo.control.managment.PortfolioEntry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PortfolioEntryTest {

    private final PortfolioEntry portfolioEntry = PortfolioEntry.builder()
            .name("ABC")
            .quantity(2)
            .purchasePrice(BigDecimal.TEN)
            .purchaseCost(BigDecimal.valueOf(30))
            .notionalSalesPrice(BigDecimal.valueOf(20))
            .build();

    @Test
    public void shouldGetSum() {
        assertThat(portfolioEntry.getSum()).isEqualTo(BigDecimal.valueOf(20));
    }

    @Test
    public void shouldGetNotationalRevenueResult() {
        assertThat(portfolioEntry.getNotionalRevenue()).isEqualTo(BigDecimal.valueOf(40));
    }

    @Test
    public void shouldGetLoss() {
        assertThat(portfolioEntry.getProfitOrLoss())
                .isNegative()
                .isEqualTo(BigDecimal.valueOf(-50));
    }

}