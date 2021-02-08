package com.example.risk.control.management;

import com.example.risk.boundary.api.RiskResult;
import com.example.risk.control.management.caclulate.RiskManagementCalculator;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RiskManagementTest {

    @Test
    void shouldReturnPositionRisk() {
        RiskManagementCalculator riskManagementCalculator = createRiskManagementCalculator(Collections.emptyList());

        BigDecimal positionRisk = riskManagementCalculator.calculatePositionRisk();

        assertThat(positionRisk).isEqualTo(BigDecimal.valueOf(2250000, 4));
    }

    private List<Investment> createInvestmentsWithTwoEntries() {
        List<Investment> investments = new ArrayList<>(2);
        investments.add(createInvestment(41, BigDecimal.valueOf(50), BigDecimal.valueOf(45), BigDecimal.valueOf(20)));
        investments.add(createInvestment(45, BigDecimal.valueOf(65), BigDecimal.valueOf(60.45), BigDecimal.valueOf(20)));

        return investments;
    }

    private List<Investment> createInvestmentsWithThreeEntries() {
        List<Investment> investments = createInvestmentsWithTwoEntries();
        investments.add(createInvestment(229, BigDecimal.valueOf(12), BigDecimal.valueOf(11.04), BigDecimal.valueOf(5)));

        return investments;
    }

    private Investment createInvestment(int quantity, BigDecimal purchaseCost,
                                        BigDecimal notionalSalesPrice, BigDecimal transactionCosts) {
        return Investment.builder()
                .quantity(quantity)
                .purchasePrice(purchaseCost)
                .currentNotionalSalesPrice(notionalSalesPrice)
                .initialNotionalSalesPrice(notionalSalesPrice)
                .transactionCosts(transactionCosts)
                .build();
    }

    @Test
    void shouldReturnTotalRiskCalculatedStaticallyWithTwoEntries2() {
        RiskManagementCalculator riskManagementCalculator = createRiskManagementCalculator(createInvestmentsWithTwoEntries());

        double totalRisk = riskManagementCalculator.calculate().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(2.99);
    }

    @Test
    void shouldReturnTotalRiskCalculatedStaticallyWithThreeEntries2() {
        RiskManagementCalculator riskManagementCalculator = createRiskManagementCalculator(createInvestmentsWithThreeEntries());

        double totalRisk = riskManagementCalculator.calculate().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(4.49);
    }

    @Test
    void shouldReturnTotalRiskCalculatedStaticallyWithThreeEntriesWhenPricesIncreased2() {
        RiskManagementCalculator riskManagementCalculator = createRiskManagementCalculator(createInvestmentsWithThreeEntriesIncreased());

        double totalRisk = riskManagementCalculator.calculate().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(0.3);
    }

    @Test
    void shouldReturnTotalRiskCalculatedStaticallyWithThreeEntriesWhenPricesDecreased2() {
        RiskManagementCalculator riskManagementCalculator = createRiskManagementCalculator(createInvestmentsWithThreeEntriesDecreased());

        double totalRisk = riskManagementCalculator.calculate().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(10.58);
    }

    private List<Investment> createInvestmentsWithThreeEntriesIncreased() {
        List<Investment> investments = new ArrayList<>(3);
        investments.add(createInvestment(41, BigDecimal.valueOf(50), BigDecimal.valueOf(50), BigDecimal.valueOf(20)));
        investments.add(createInvestment(45, BigDecimal.valueOf(65), BigDecimal.valueOf(65), BigDecimal.valueOf(20)));
        investments.add(createInvestment(229, BigDecimal.valueOf(12), BigDecimal.valueOf(12), BigDecimal.valueOf(5)));

        return investments;
    }

    private List<Investment> createInvestmentsWithThreeEntriesDecreased() {
        List<Investment> investments = new ArrayList<>(3);
        investments.add(createInvestment(41, BigDecimal.valueOf(50), BigDecimal.valueOf(40), BigDecimal.valueOf(20)));
        investments.add(createInvestment(45, BigDecimal.valueOf(65), BigDecimal.valueOf(50), BigDecimal.valueOf(20)));
        investments.add(createInvestment(229, BigDecimal.valueOf(12), BigDecimal.valueOf(10), BigDecimal.valueOf(5)));

        return investments;
    }

    @Test
    void shouldReturnCalculateDepotRisk() {
        RiskManagementCalculator riskManagementCalculator = createRiskManagementCalculator(createInvestmentsWithThreeEntries());

        RiskResult riskResult = riskManagementCalculator.calculate();

        assertThat(riskResult.getDepotRisk()).isEqualTo(BigDecimal.valueOf(674.59));
        assertThat(riskResult.getDepotRiskInPercent()).isEqualTo(9.51);
    }

    @Test
    void shouldReturnCalculateDepotRiskCalculatedDynamicallyWhenPricesIncreased() {
        RiskManagementCalculator riskManagementCalculator = createRiskManagementCalculator(createInvestmentsWithThreeEntriesIncreased());

        RiskResult riskResult = riskManagementCalculator.calculate();

        assertThat(riskResult.getDepotRisk()).isEqualTo(BigDecimal.valueOf(45));
        assertThat(riskResult.getDepotRiskInPercent()).isEqualTo(0.58);
    }

    @Test
    void shouldReturnCalculateDepotRiskCalculatedDynamicallyWhenPricesDecreased() {
        RiskManagementCalculator riskManagementCalculator = createRiskManagementCalculator(createInvestmentsWithThreeEntriesDecreased());

        RiskResult riskResult = riskManagementCalculator.calculate();

        assertThat(riskResult.getDepotRisk()).isEqualTo(BigDecimal.valueOf(1588));
        assertThat(riskResult.getDepotRiskInPercent()).isEqualTo(25.69);
    }

    @Test
    void shouldReturnCalculatedTotalRisk() {
        RiskManagementCalculator riskManagementCalculator = createRiskManagementCalculator(createInvestmentsWithThreeEntries());

        double totalRisk = riskManagementCalculator.calculate().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(4.49);
    }

    @Test
    void shouldReturnCalculatedTotalRiskCalculatedDynamicallyWhenPricesIncreased() {
        RiskManagementCalculator riskManagementCalculator = createRiskManagementCalculator(createInvestmentsWithThreeEntriesIncreased());

        double totalRisk = riskManagementCalculator.calculate().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(0.3);
    }

    @Test
    void shouldReturnCalculatedTotalRiskCalculatedDynamicallyWhenPricesDecreased() {
        RiskManagementCalculator riskManagementCalculator = createRiskManagementCalculator(createInvestmentsWithThreeEntriesDecreased());

        double totalRisk = riskManagementCalculator.calculate().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(10.58);
    }

    private RiskManagementCalculator createRiskManagementCalculator(List<Investment> investmentsWithThreeEntriesDecreased) {
        return new RiskManagementCalculator(
                new IndividualRisk(BigDecimal.valueOf(15000), 1.5),
                investmentsWithThreeEntriesDecreased);
    }

}