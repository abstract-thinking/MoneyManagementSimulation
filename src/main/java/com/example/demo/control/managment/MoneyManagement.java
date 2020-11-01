package com.example.demo.control.managment;

import java.math.BigDecimal;
import java.util.Collections;

import static java.util.Objects.requireNonNull;

public class MoneyManagement {

    private final BigDecimal totalCapital;

    private final double individualPositionRiskInPercent;

    private final Portfolio portfolio;

    public MoneyManagement(BigDecimal totalCapital, double individualPositionRiskInPercent) {
        this(totalCapital, individualPositionRiskInPercent, new Portfolio(Collections.emptyList()));
    }

    public MoneyManagement(BigDecimal totalCapital, double individualPositionRiskInPercent, Portfolio portfolio) {
        this.totalCapital = requireNonNull(totalCapital);
        this.individualPositionRiskInPercent = individualPositionRiskInPercent;
        this.portfolio = requireNonNull(portfolio);
    }

    public BigDecimal getIndividualPositionRisk() {
        return totalCapital.multiply(BigDecimal.valueOf(individualPositionRiskInPercent / 100));
    }

    public double getPortfolioRiskInPercent() {
        return portfolio.getTotalLossAbs().doubleValue() / portfolio.getTotalSum().doubleValue() * 100;
    }

    public double getTotalRiskInPercent() {
        return portfolio.getTotalLossAbs().doubleValue() / totalCapital.doubleValue() * 100;
    }
}
