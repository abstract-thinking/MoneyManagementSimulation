package com.example.demo.control.managment;

import java.math.BigDecimal;

import static com.example.demo.control.invest.PriceCalculator.calculateNotionalSalesPrice;
import static java.math.RoundingMode.DOWN;

public class QuantityCalculator {

    public static int calculateQuantity(double rsl, BigDecimal currentPrice, double exchangeRsl,
                                                BigDecimal individualPositionRisk, BigDecimal purchaseCost) {
        BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(rsl, currentPrice, exchangeRsl);
        BigDecimal notionalLoss = currentPrice.subtract(notionalSalesPrice);
        BigDecimal positionRisk = individualPositionRisk.subtract(purchaseCost);

        return positionRisk.divide(notionalLoss, DOWN).intValue();
    }
}
