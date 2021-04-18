package com.example.risk.control.management.calculate;

import com.example.risk.boundary.api.CalculationResult;
import com.example.risk.data.IndividualRisk;
import com.example.risk.service.finanztreff.ExchangeSnapshot;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

import static com.example.risk.control.management.calculate.MoneyManagement.calculateQuantity;
import static com.example.risk.control.management.calculate.PriceCalculator.calculateNotionalSalesPrice;

@AllArgsConstructor
public class PositionCalculator {

    private final ExchangeSnapshot exchangeSnapshot;

    public CalculationResult calculate(String wkn, IndividualRisk individualRisk) {
        final ExchangeSnapshot.Quotes foundExchangeData = findExchangeData(wkn);
        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(
                foundExchangeData.getRsl(), foundExchangeData.getPrice(), exchangeSnapshot.getRsl());

        final MoneyManagement.Parameters purchaseParameters = new MoneyManagement.Parameters(
                foundExchangeData.getPrice(), notionalSalesPrice, exchangeSnapshot.getTransactionCosts());
        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), purchaseParameters);

        return createResult(individualRisk, foundExchangeData, notionalSalesPrice, quantity);
    }

    private ExchangeSnapshot.Quotes findExchangeData(String wkn) {
        return exchangeSnapshot.getQuotes().stream()
                .filter(data -> data.getWkn().equalsIgnoreCase(wkn))
                .findFirst()
                .orElseThrow();
    }

    private CalculationResult createResult(IndividualRisk individualRisk, ExchangeSnapshot.Quotes foundExchangeData, BigDecimal notionalSalesPrice, int quantity) {
        return CalculationResult.builder()
                .wkn(foundExchangeData.getWkn())
                .name(foundExchangeData.getName())
                .price(foundExchangeData.getPrice())
                .quantity(quantity)
                .notionalSalesPrice(notionalSalesPrice)
                .transactionCosts(exchangeSnapshot.getTransactionCosts())
                .rsl(foundExchangeData.getRsl())
                .exchangeRsl(exchangeSnapshot.getRsl())
                .positionRisk(individualRisk.calculateIndividualPositionRisk())
                .build();
    }
}