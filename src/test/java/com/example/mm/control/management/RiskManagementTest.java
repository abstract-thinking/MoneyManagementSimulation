package com.example.mm.control.management;

import com.example.mm.boundary.RiskManagementResult;
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
        BigDecimal positionRisk = riskManagement.calculatePositionRisk();

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
    void shouldReturnTotalRiskCalculatedStaticallyWithTwoEntries2() {
        riskManagement.setInvestments(createInvestmentsWithTwoEntries());

        double totalRisk = riskManagement.toApi().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(2.99);
    }

    @Test
    void shouldReturnTotalRiskCalculatedStaticallyWithThreeEntries2() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntries());

        double totalRisk = riskManagement.toApi().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(4.49);
    }

    @Test
    void shouldReturnTotalRiskCalculatedStaticallyWithThreeEntriesWhenPricesIncreased2() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesIncreased());

        double totalRisk = riskManagement.toApi().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(0.3);
    }

    @Test
    void shouldReturnTotalRiskCalculatedStaticallyWithThreeEntriesWhenPricesDecreased2() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesDecreased());

        double totalRisk = riskManagement.toApi().getTotalRiskInPercent();

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
        riskManagement.setInvestments(createInvestmentsWithThreeEntries());

        RiskManagementResult riskManagementResult = riskManagement.toApi();

        assertThat(riskManagementResult.getDepotRisk()).isEqualTo(BigDecimal.valueOf(674.59));
        assertThat(riskManagementResult.getDepotRiskInPercent()).isEqualTo(9.51);
    }

    @Test
    void shouldReturnCalculateDepotRiskCalculatedDynamicallyWhenPricesIncreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesIncreased());

        RiskManagementResult riskManagementResult = riskManagement.toApi();

        assertThat(riskManagementResult.getDepotRisk()).isEqualTo(BigDecimal.valueOf(45));
        assertThat(riskManagementResult.getDepotRiskInPercent()).isEqualTo(0.58);
    }

    @Test
    void shouldReturnCalculateDepotRiskCalculatedDynamicallyWhenPricesDecreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesDecreased());

        RiskManagementResult riskManagementResult = riskManagement.toApi();

        assertThat(riskManagementResult.getDepotRisk()).isEqualTo(BigDecimal.valueOf(1588));
        assertThat(riskManagementResult.getDepotRiskInPercent()).isEqualTo(25.69);
    }

    @Test
    void shouldReturnCalculatedTotalRisk() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntries());

        double totalRisk = riskManagement.toApi().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(4.49);
    }

    @Test
    void shouldReturnCalculatedTotalRiskCalculatedDynamicallyWhenPricesIncreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesIncreased());

        double totalRisk = riskManagement.toApi().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(0.3);
    }

    @Test
    void shouldReturnCalculatedTotalRiskCalculatedDynamicallyWhenPricesDecreased() {
        riskManagement.setInvestments(createInvestmentsWithThreeEntriesDecreased());

        double totalRisk = riskManagement.toApi().getTotalRiskInPercent();

        assertThat(totalRisk).isEqualTo(10.58);
    }

}