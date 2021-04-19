package com.example.risk.service;

import com.example.risk.boundary.api.CalculationResult;
import com.example.risk.boundary.api.ExchangeResult2;
import com.example.risk.data.IndividualRisk;
import com.example.risk.service.finanztreff.ExchangeSnapshot;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.example.risk.service.MoneyManagement.calculateQuantity;
import static com.example.risk.service.PriceCalculator.calculateNotionalSalesPrice;

@Service
public class PositionCalculator {

    public CalculationResult calculate(ExchangeSnapshot snapshot, String wkn, IndividualRisk individualRisk) {
        final ExchangeSnapshot.Quotes foundExchangeData = findExchangeData(snapshot, wkn);
        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(
                foundExchangeData.getRsl(), foundExchangeData.getPrice(), snapshot.getExchange().getRsl());

        final MoneyManagement.Parameters purchaseParameters = new MoneyManagement.Parameters(
                foundExchangeData.getPrice(), notionalSalesPrice, snapshot.getExchange().getTransactionCosts());
        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), purchaseParameters);

        return createResult(snapshot, individualRisk, foundExchangeData, notionalSalesPrice, quantity);
    }

    private ExchangeSnapshot.Quotes findExchangeData(ExchangeSnapshot exchangeSnapshot, String wkn) {
        return exchangeSnapshot.getQuotes().stream()
                .filter(data -> data.getWkn().equalsIgnoreCase(wkn))
                .findFirst()
                .orElseThrow();
    }

    private CalculationResult createResult(ExchangeSnapshot snapshot, IndividualRisk individualRisk, ExchangeSnapshot.Quotes foundExchangeData, BigDecimal notionalSalesPrice, int quantity) {
        return CalculationResult.builder()
                .wkn(foundExchangeData.getWkn())
                .name(foundExchangeData.getName())
                .price(foundExchangeData.getPrice())
                .quantity(quantity)
                .notionalSalesPrice(notionalSalesPrice)
                .transactionCosts(snapshot.getExchange().getTransactionCosts())
                .rsl(foundExchangeData.getRsl())
                .exchangeResult(new ExchangeResult2(snapshot.getExchange().getName(), snapshot.getExchange().getRsl()))
                .positionRisk(individualRisk.calculateIndividualPositionRisk())
                .build();
    }
}