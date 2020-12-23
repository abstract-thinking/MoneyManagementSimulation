package com.example.mm.control.management;

import com.example.mm.data.Investment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RiskManagementTest {

    private final RiskManagement riskManagement = new RiskManagement(BigDecimal.valueOf(15000), 1.5);

    @Test
    void shouldReturnPositionRisk() {
        BigDecimal positionRisk = riskManagement.getPositionRisk();

        assertThat(positionRisk).isEqualTo(BigDecimal.valueOf(2250000, 4));
    }

    @Test
    void shouldReturnDepotRiskCalculatedStaticallyWithTwoEntries() {
        riskManagement.setInvestments(createInvestmentsWithTwoEntries());

        double depotRisk = riskManagement.getDepotRiskInPercent();

        assertThat(depotRisk).isEqualTo(9.04);
    }

    @Test
    void shouldReturnDepotRiskCalculatedStaticallyWithThreeEntries() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntries());

        double depotRisk = riskManagement.getDepotRiskInPercent();

        assertThat(depotRisk).isEqualTo(8.74);
    }

    @Test
    void shouldReturnDepotRiskCalculatedStaticallyWithThreeEntriesWhenPricesIncreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesIncreased());

        double depotRisk = riskManagement.getDepotRiskInPercent();

        assertThat(depotRisk).isEqualTo(8.74);
    }

    @Test
    void shouldReturnDepotRiskCalculatedStaticallyWithThreeEntriesWhenPricesDecreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesDecreased());

        double depotRisk = riskManagement.getDepotRiskInPercent();

        assertThat(depotRisk).isEqualTo(8.74);
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

    private Investment createInvestment(
            int quantity, BigDecimal purchaseCost, BigDecimal notionalSalesPrice, BigDecimal transactionCosts) {
        return Investment.builder()
                .quantity(quantity)
                .purchasePrice(purchaseCost)
                .notionalSalesPrice(notionalSalesPrice)
                .transactionCosts(transactionCosts)
                .build();
    }

    @Test
    void shouldReturnTotalRiskCalculatedStaticallyWithTwoEntries() {
        riskManagement.setInvestments(createInvestmentsWithTwoEntries());

        double totalRisk = riskManagement.getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(3.0);
    }

    @Test
    void shouldReturnTotalRiskCalculatedStaticallyWithThreeEntries() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntries());

        double totalRisk = riskManagement.getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(4.5);
    }

    @Test
    void shouldReturnTotalRiskCalculatedStaticallyWithThreeEntriesWhenPricesIncreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesIncreased());

        double totalRisk = riskManagement.getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(4.5);
    }

    @Test
    void shouldReturnTotalRiskCalculatedStaticallyWithThreeEntriesWhenPricesDecreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesDecreased());

        double totalRisk = riskManagement.getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(4.5);
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
    void shouldReturnRecalculateDepotRisk() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntries());

        BigDecimal depotRisk = riskManagement.recalculateDepotRisk();

        assertThat(depotRisk).isEqualTo(BigDecimal.valueOf(674.59));
    }

    @Test
    void shouldReturnRecalculateDepotRiskCalculatedDynamicallyWhenPricesIncreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesIncreased());

        BigDecimal depotRisk = riskManagement.recalculateDepotRisk();

        assertThat(depotRisk).isEqualTo(BigDecimal.valueOf(45));
    }

    @Test
    void shouldReturnRecalculateDepotRiskCalculatedDynamicallyWhenPricesDecreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesDecreased());

        BigDecimal depotRisk = riskManagement.recalculateDepotRisk();

        assertThat(depotRisk).isEqualTo(BigDecimal.valueOf(1588));
    }

    @Test
    void shouldReturnRecalculatedTotalRisk() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntries());

        double totalRisk = riskManagement.recalculateTotalRisk();

        assertThat(totalRisk).isEqualTo(4.7);
    }

    @Test
    void shouldReturnRecalculatedTotalRiskCalculatedDynamicallyWhenPricesIncreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesIncreased());

        double totalRisk = riskManagement.recalculateTotalRisk();

        assertThat(totalRisk).isEqualTo(0.3);
    }

    @Test
    void shouldReturnRecalculatedTotalRiskCalculatedDynamicallyWhenPricesDecreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesDecreased());

        double totalRisk = riskManagement.recalculateTotalRisk();

        assertThat(totalRisk).isEqualTo(11.84);
    }

}