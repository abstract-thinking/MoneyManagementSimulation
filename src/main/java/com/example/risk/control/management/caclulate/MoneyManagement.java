package com.example.risk.control.management.caclulate;

import com.example.risk.data.Investment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoneyManagement {

    public static int calculateQuantity(BigDecimal relativePositionRisk, Investment possibleInvestment) {
        BigDecimal absolutePositionRisk = relativePositionRisk.subtract(possibleInvestment.getTransactionCosts());

        return absolutePositionRisk.divide(possibleInvestment.getPriceRisk(), 4, RoundingMode.DOWN).intValue();
    }

}