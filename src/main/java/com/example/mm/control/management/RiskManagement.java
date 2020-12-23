package com.example.mm.control.management;

import com.example.mm.boundary.RiskManagementApi;
import com.example.mm.data.Investment;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;

public class RiskManagement {

    private final BigDecimal totalCapital;

    private final double positionRiskInPercent;

    @Setter
    private List<Investment> investments = new ArrayList<>();

    public RiskManagement(BigDecimal totalCapital, double positionRiskInPercent) {
        this.totalCapital = totalCapital;
        this.positionRiskInPercent = positionRiskInPercent;
    }

    public BigDecimal getPositionRisk() {
        return totalCapital
                .multiply(BigDecimal.valueOf(positionRiskInPercent))
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.DOWN);
    }

    public BigDecimal getDepotRisk() {
        BigDecimal capitalInvestment = getTotalInvestment();

        return calculateDepotRisk()
                .divide(capitalInvestment, 4, RoundingMode.DOWN);
    }

    public double getDepotRiskInPercent() {
        return getDepotRisk().multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    public BigDecimal getTotalRisk() {
        return calculateDepotRisk().divide(totalCapital, 4, RoundingMode.DOWN);
    }

    public double getTotalRiskInPercent() {
        return getTotalRisk().multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    private BigDecimal calculateDepotRisk() {
        return getPositionRisk()
                .multiply(BigDecimal.valueOf(investments.size()));
    }

    public BigDecimal recalculateDepotRisk() {
        return investments.stream()
                .map(Investment::getRisk)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public double recalculateTotalRisk() {
        return recalculateDepotRisk()
                .divide(recalculatedTotalCapital(), 4, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private BigDecimal recalculatedTotalCapital() {
        BigDecimal notionalRevenue = getTotalRevenue();

        BigDecimal transactionCosts = investments.stream()
                .map(Investment::getTransactionCosts)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal investmentCapital = getTotalInvestment();

        BigDecimal freeCapital = totalCapital.subtract(investmentCapital);

        return notionalRevenue
                .subtract(transactionCosts)
                .add(freeCapital);
    }

    public RiskManagementApi toApi() {
        return RiskManagementApi.builder()
                .totalCapital(totalCapital)
                .individualPositionRiskInPercent(positionRiskInPercent)
                .individualPositionRisk(getPositionRisk())
                .totalInvestment(getTotalInvestment())
                .totalRevenue(getTotalRevenue())
                .totalLossAbs(getTotalLossAbs())
                .depotRisk(getDepotRisk())
                .depotRiskInPercent(getDepotRiskInPercent())
                .totalRisk(getTotalRisk())
                .totalRiskInPercent(getTotalRiskInPercent())
                .build();
    }

    public BigDecimal getTotalInvestment() {
        return investments.stream()
                .map(Investment::getInvestment)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalRevenue() {
        return investments.stream()
                .map(Investment::getNotionalRevenue)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalLossAbs() {
        return investments.stream()
                .map(Investment::getRisk)
                .reduce(ZERO, BigDecimal::add)
                .abs();
    }

}
