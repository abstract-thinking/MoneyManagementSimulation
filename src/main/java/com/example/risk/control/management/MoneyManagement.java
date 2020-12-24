package com.example.risk.control.management;

import com.example.risk.data.Investment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoneyManagement {

    public static int calculateQuantity(BigDecimal positionRisk, Investment possibleInvestment) {
        BigDecimal absolutePositionRisk = positionRisk.subtract(possibleInvestment.getTransactionCosts());

        return absolutePositionRisk.divide(possibleInvestment.getPositionRisk(), 4, RoundingMode.DOWN).intValue();
    }

}