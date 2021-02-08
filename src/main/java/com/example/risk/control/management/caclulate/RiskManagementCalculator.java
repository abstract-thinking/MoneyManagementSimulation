package com.example.risk.control.management.caclulate;

import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

public class RiskManagementCalculator {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final IndividualRisk individualRisk;

    @Getter
    private final List<Investment> investments;

    public RiskManagementCalculator(IndividualRisk individualRisk, List<Investment> investments) {
        this.individualRisk = individualRisk;
        this.investments = investments;
    }

    public BigDecimal calculatePositionRisk() {
        return individualRisk.getTotalCapital()
                .multiply(BigDecimal.valueOf(individualRisk.getIndividualPositionRiskInPercent()))
                .divide(ONE_HUNDRED, 4, RoundingMode.DOWN);
    }

    private BigDecimal calculateDepotRisk() {
        return investments.stream()
                .map(Investment::getRisk)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs();
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

    public RiskResult calculate() {
        return RiskResult.builder()
                .id(individualRisk.getId())
                .totalCapital(individualRisk.getTotalCapital())
                .individualPositionRiskInPercent(individualRisk.getIndividualPositionRiskInPercent())
                .individualPositionRisk(calculatePositionRisk())
                .investments(map(investments))
                .totalInvestment(calculateTotalInvestment())
                .totalRevenue(calculateTotalNotionalRevenue())
                .depotRisk(calculateDepotRisk())
                .depotRiskInPercent(calculateDepotRiskInPercent())
                .totalRiskInPercent(calculateTotalRiskInPercent())
                .build();
    }

    private List<InvestmentResult> map(List<Investment> investments) {
        return investments.stream()
                .map(Investment::toApi)
                .collect(Collectors.toList());
    }
}
