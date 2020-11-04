package com.example.demo.control.management;

import com.example.demo.control.managment.MoneyManagement;
import com.example.demo.data.MoneyManagementValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.demo.control.management.InvestmentsTest.createInvestments;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class MoneyManagementTest {

    private MoneyManagement moneyManagement;

    @BeforeEach
    private void setUp() {
        MoneyManagementValues moneyManagementValues = MoneyManagementValues.builder()
                .totalCapital(BigDecimal.valueOf(30000))
                .individualPositionRiskInPercent(2)
                .build();

        moneyManagement = new MoneyManagement(moneyManagementValues, createInvestments());
    }

    @Test
    public void getIndividualPositionRisk() {
        BigDecimal individualPositionRisk = moneyManagement.getIndividualPositionRisk();

        assertThat(individualPositionRisk).isEqualByComparingTo(BigDecimal.valueOf(600.00));
    }

    @Test
    public void getPortfolioRisk() {
        double portfolioRisk = moneyManagement.getPortfolioRiskInPercent();

        assertThat(portfolioRisk).isCloseTo(91.818, offset(0.001));
    }

    @Test
    public void getTotalRisk() {
        double totalRisk = moneyManagement.getTotalRiskInPercent();

        assertThat(totalRisk).isCloseTo(0.336, offset(0.001));
    }

}
