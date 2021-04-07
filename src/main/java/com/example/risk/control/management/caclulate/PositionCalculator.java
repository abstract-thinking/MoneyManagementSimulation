package com.example.risk.control.management.caclulate;

import com.example.risk.boundary.api.CalculationResult;
import com.example.risk.converter.ExchangeData;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

import static com.example.risk.control.management.caclulate.MoneyManagement.calculateQuantity;
import static com.example.risk.control.management.caclulate.PriceCalculator.calculateNotionalSalesPrice;

@AllArgsConstructor
public class PositionCalculator {

    private final ExchangeSnapshot exchangeSnapshot;

    public CalculationResult calculate(String wkn, IndividualRisk individualRisk) {
        final ExchangeData foundExchangeData = exchangeSnapshot.findExchangeData(wkn);

        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(
                foundExchangeData.getRsl(), foundExchangeData.getPrice(), exchangeSnapshot.getExchangeRsl());

        final Investment possibleInvestment = Investment.builder()
                .purchasePrice(foundExchangeData.getPrice())
                .stopPrice(notionalSalesPrice)
                .transactionCosts(exchangeSnapshot.getExchangeTransactionCosts())
                .build();

        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), possibleInvestment);

        return createResult(individualRisk, foundExchangeData, notionalSalesPrice, quantity);
    }

    private CalculationResult createResult(IndividualRisk individualRisk, ExchangeData foundExchangeData, BigDecimal notionalSalesPrice, int quantity) {
        return CalculationResult.builder()
                .wkn(foundExchangeData.getWkn())
                .name(foundExchangeData.getName())
                .price(foundExchangeData.getPrice())
                .quantity(quantity)
                .notionalSalesPrice(notionalSalesPrice)
                .transactionCosts(exchangeSnapshot.getExchangeTransactionCosts())
                .rsl(foundExchangeData.getRsl())
                .exchangeRsl(exchangeSnapshot.getExchangeRsl())
                .positionRisk(individualRisk.calculateIndividualPositionRisk())
                .build();
    }

}