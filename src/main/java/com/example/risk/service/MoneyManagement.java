package com.example.risk.service;

import lombok.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MoneyManagement {

    public static int calculateQuantity(BigDecimal relativePositionRisk, Parameters parameters) {
        return relativePositionRisk.subtract(parameters.getTransactionCosts())
                .divide(parameters.getPriceRisk(), 4, RoundingMode.DOWN).intValue();
    }

    @Value
    public static class Parameters {

        BigDecimal purchasePrice;
        BigDecimal stopPrice;

        BigDecimal transactionCosts;

        public BigDecimal getPriceRisk() {
            return purchasePrice.subtract(stopPrice);
        }
    }
}