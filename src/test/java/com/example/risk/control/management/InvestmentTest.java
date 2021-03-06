package com.example.risk.control.management;

import com.example.risk.data.Investment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class InvestmentTest {

    private final Investment investment = Investment.builder()
            .name("Positive Investment AG")
            .quantity(10)
            .purchasePrice(BigDecimal.TEN)
            .transactionCosts(BigDecimal.ONE)
            .stopPrice(BigDecimal.valueOf(20))
            .build();

    @Test
    void shouldGetInvestment() {
        BigDecimal investment = this.investment.getInvestmentCapital();

        assertThat(investment).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void shouldReturnInvestment() {
        Investment investment = Investment.builder()
                .quantity(41)
                .purchasePrice(BigDecimal.valueOf(50))
                .build();

        assertThat(investment.getInvestmentCapital()).isEqualTo(BigDecimal.valueOf(2050));
    }


}