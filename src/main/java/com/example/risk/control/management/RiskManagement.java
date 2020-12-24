package com.example.risk.control.management;

import com.example.risk.boundary.api.RiskManagementResult;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;

public class RiskManagement {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final IndividualRisk individualRisk;

    @Setter
    @Getter
    private List<Investment> investments = new ArrayList<>();

    public RiskManagement(IndividualRisk individualRisk) {
        this.individualRisk = individualRisk;
    }

    public BigDecimal calculatePositionRisk() {
        return individualRisk.getTotalCapital()
                .multiply(BigDecimal.valueOf(individualRisk.getIndividualPositionRiskInPercent()))
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
                .divide(individualRisk.getTotalCapital(), 4, RoundingMode.DOWN)
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
                .id(individualRisk.getId())
                .totalCapital(individualRisk.getTotalCapital())
                .individualPositionRiskInPercent(individualRisk.getIndividualPositionRiskInPercent())
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
