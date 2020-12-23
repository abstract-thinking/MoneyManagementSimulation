package com.example.mm.control.management;

import com.example.mm.data.Investment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoneyManagement {

    public static int calculateQuantity(BigDecimal positionRisk, Investment investment) {
        BigDecimal absolutePositionRisk = positionRisk.subtract(investment.getTransactionCosts());

        return absolutePositionRisk.divide(investment.getPositionRisk(), 4, RoundingMode.DOWN).intValue();
    }

}