package com.example.risk.control.management;

import com.example.risk.control.management.caclulate.MoneyManagement;
import com.example.risk.data.Investment;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class IndividualRiskTest {

    private static final BigDecimal POSITION_RISK = BigDecimal.valueOf(225);

    @Test
    void shouldCalculateQuantity1() {
        Investment investment = Investment.builder()
                .purchasePrice(BigDecimal.valueOf(50))
                .transactionCosts(BigDecimal.valueOf(20))
                .build();

        int quantity = MoneyManagement.calculateQuantity(POSITION_RISK, investment);

        assertThat(quantity).isEqualTo(41);
    }

    @Test
    void shouldCalculateQuantity2() {
        Investment investment = Investment.builder()
                .purchasePrice(BigDecimal.valueOf(65))
                .transactionCosts(BigDecimal.valueOf(20))
                .build();

        int quantity = MoneyManagement.calculateQuantity(POSITION_RISK, investment);

        assertThat(quantity).isEqualTo(45);
    }

    @Test
    void shouldCalculateQuantity3() {
        Investment investment = Investment.builder()
                .purchasePrice(BigDecimal.valueOf(12))
                .transactionCosts(BigDecimal.valueOf(5))
                .build();

        int quantity = MoneyManagement.calculateQuantity(POSITION_RISK, investment);

        assertThat(quantity).isEqualTo(229);
    }

}
