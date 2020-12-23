package com.example.mm.control.management;

import com.example.mm.data.Investment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyManagementTest {

    private static final BigDecimal POSITION_RISK = BigDecimal.valueOf(225);

    @Test
    void shouldCalculateQuantity1() {
        Investment investment = Investment.builder()
                .purchasePrice(BigDecimal.valueOf(50))
                .notionalSalesPrice(BigDecimal.valueOf(45))
                .transactionCosts(BigDecimal.valueOf(20))
                .build();

        int quantity = MoneyManagement.calculateQuantity(POSITION_RISK, investment);

        assertThat(quantity).isEqualTo(41);
    }

    @Test
    void shouldCalculateQuantity2() {
        Investment investment = Investment.builder()
                .purchasePrice(BigDecimal.valueOf(65))
                .notionalSalesPrice(BigDecimal.valueOf(60.45))
                .transactionCosts(BigDecimal.valueOf(20))
                .build();

        int quantity = MoneyManagement.calculateQuantity(POSITION_RISK, investment);

        assertThat(quantity).isEqualTo(45);
    }

    @Test
    void shouldCalculateQuantity3() {
        Investment investment = Investment.builder()
                .purchasePrice(BigDecimal.valueOf(12))
                .notionalSalesPrice(BigDecimal.valueOf(11.04))
                .transactionCosts(BigDecimal.valueOf(5))
                .build();

        int quantity = MoneyManagement.calculateQuantity(POSITION_RISK, investment);

        assertThat(quantity).isEqualTo(229);
    }

}
