package com.example.mm.control.management;

import com.example.mm.boundary.RiskManagementResult;
import com.example.mm.data.Investment;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;

public class RiskManagement {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final BigDecimal totalCapital;

    private final double positionRiskInPercent;

    @Setter
    private List<Investment> investments = new ArrayList<>();

    public RiskManagement(BigDecimal totalCapital, double positionRiskInPercent) {
        this.totalCapital = totalCapital;
        this.positionRiskInPercent = positionRiskInPercent;
    }

    public BigDecimal calculatePositionRisk() {
        return totalCapital
                .multiply(BigDecimal.valueOf(positionRiskInPercent))
                .divide(ONE_HUNDRED, 4, RoundingMode.DOWN);
    }

    private BigDecimal calculateDepotRisk() {
        return investments.stream()
                .map(Investment::getRisk)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private double calculateDepotRiskInPercent() {
        return calculateDepotRisk()
                .divide(calculateTotalNotionalRevenue(), 4, RoundingMode.DOWN)
                .multiply(ONE_HUNDRED)
                .doubleValue();
    }

    private double calculateTotalRiskInPercent() {
        return calculateDepotRisk()
                .divide(totalCapital, 4, RoundingMode.DOWN)
                .multiply(ONE_HUNDRED).doubleValue();
    }

    private BigDecimal calculateTotalInvestment() {
        return investments.stream()
                .map(Investment::getInvestment)
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalNotionalRevenue() {
        return investments.stream()
                .map(Investment::getNotionalRevenue)
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalLossAbs() {
        return investments.stream()
                .map(Investment::getRisk)
                .reduce(ZERO, BigDecimal::add)
                .abs();
    }

    public RiskManagementResult toApi() {
        return RiskManagementResult.builder()
                .totalCapital(totalCapital)
                .individualPositionRiskInPercent(positionRiskInPercent)
                .individualPositionRisk(calculatePositionRisk())
                .investments(investments)
                .totalInvestment(calculateTotalInvestment())
                .totalRevenue(calculateTotalNotionalRevenue())
                .totalLossAbs(calculateTotalLossAbs())
                .depotRisk(calculateDepotRisk())
                .depotRiskInPercent(calculateDepotRiskInPercent())
                .totalRiskInPercent(calculateTotalRiskInPercent())
                .build();
    }
}
