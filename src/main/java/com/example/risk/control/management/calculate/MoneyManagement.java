package com.example.risk.control.management.calculate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoneyManagement {

    public static int calculateQuantity(BigDecimal relativePositionRisk, Parameters parameters) {
        return relativePositionRisk.subtract(parameters.getTransactionCosts())
                .divide(parameters.getPriceRisk(), 4, RoundingMode.DOWN).intValue();
    }

    @Value
    static class Parameters {

        BigDecimal purchasePrice;
        BigDecimal stopPrice;

        BigDecimal transactionCosts;

        public BigDecimal getPriceRisk() {
            return purchasePrice.subtract(stopPrice);
        }
    }
}