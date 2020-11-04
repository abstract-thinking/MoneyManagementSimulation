package com.example.demo.control.management;

import com.example.demo.data.Investment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InvestmentTest {

    private final Investment investment = Investment.builder()
            .name("ABC")
            .quantity(2)
            .purchasePrice(BigDecimal.TEN)
            .purchaseCost(BigDecimal.valueOf(30))
            .notionalSalesPrice(BigDecimal.valueOf(20))
            .build();

    @Test
    public void shouldGetSum() {
        assertThat(investment.getSum()).isEqualTo(BigDecimal.valueOf(20));
    }

    @Test
    public void shouldGetNotationalRevenueResult() {
        assertThat(investment.getNotionalRevenue()).isEqualTo(BigDecimal.valueOf(40));
    }

    @Test
    public void shouldGetLoss() {
        assertThat(investment.getProfitOrLoss())
                .isNegative()
                .isEqualTo(BigDecimal.valueOf(-10));
    }

}