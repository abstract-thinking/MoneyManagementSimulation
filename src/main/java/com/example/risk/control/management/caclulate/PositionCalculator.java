package com.example.risk.control.management.caclulate;

import com.example.risk.boundary.api.CalculationResult;
import com.example.risk.converter.ExchangeData;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;

import java.math.BigDecimal;
import java.util.List;

import static com.example.risk.control.management.caclulate.MoneyManagement.calculateQuantity;
import static com.example.risk.control.management.caclulate.PriceCalculator.calculateNotionalSalesPrice;

public class PositionCalculator extends Calculator {

    public PositionCalculator(List<ExchangeData> exchangeData) {
        super(exchangeData);
    }

    public CalculationResult explain(String wkn, IndividualRisk individualRisk) {
        final ExchangeData foundExchangeData = exchangeData.stream()
                .filter(row -> row.getWkn().equalsIgnoreCase(wkn))
                .findFirst()
                .orElseThrow();

        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(
                foundExchangeData.getRsl(), foundExchangeData.getPrice(), exchangeRsl);

        final Investment possibleInvestment = Investment.builder()
                .purchasePrice(foundExchangeData.getPrice())
                .stopPrice(notionalSalesPrice)
                .transactionCosts(EXCHANGE_TRANSACTION_COSTS)
                .build();

        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), possibleInvestment);

        return CalculationResult.builder()
                .wkn(foundExchangeData.getWkn())
                .name(foundExchangeData.getName())
                .price(foundExchangeData.getPrice())
                .quantity(quantity)
                .notionalSalesPrice(notionalSalesPrice)
                .transactionCosts(EXCHANGE_TRANSACTION_COSTS)
                .rsl(foundExchangeData.getRsl())
                .exchangeRsl(exchangeRsl)
                .positionRisk(individualRisk.calculateIndividualPositionRisk())
                .build();
    }
}
