package com.example.demo.control.managment;

import com.example.demo.data.MoneyManagementValues;

import java.math.BigDecimal;
import java.util.Collections;

import static java.util.Objects.requireNonNull;

public class MoneyManagement {

    private final BigDecimal totalCapital;

    private final double individualPositionRiskInPercent;

    private final Investments investments;

    public MoneyManagement(MoneyManagementValues moneyManagementValues) {
        this(moneyManagementValues, new Investments(Collections.emptyList()));
    }

    public MoneyManagement(MoneyManagementValues moneyManagementValues, Investments investments) {
        this.totalCapital = requireNonNull(moneyManagementValues.getTotalCapital());
        this.individualPositionRiskInPercent = moneyManagementValues.getIndividualPositionRiskInPercent();
        this.investments = requireNonNull(investments);
    }

    public BigDecimal getIndividualPositionRisk() {
        return totalCapital.multiply(BigDecimal.valueOf(individualPositionRiskInPercent / 100));
    }

    public double getPortfolioRiskInPercent() {
        return investments.getTotalLossAbs().doubleValue() / investments.getTotalSum().doubleValue() * 100;
    }

    public double getTotalRiskInPercent() {
        return investments.getTotalLossAbs().doubleValue() / totalCapital.doubleValue() * 100;
    }
}
