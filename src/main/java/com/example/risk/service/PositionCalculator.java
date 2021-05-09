package com.example.risk.service;

import com.example.risk.boundary.api.CalculationResult;
import com.example.risk.boundary.api.Exchange;
import com.example.risk.boundary.api.ExchangeResult;
import com.example.risk.boundary.api.QueryResult;
import com.example.risk.data.IndividualRisk;
import com.example.risk.service.finanztreff.ExchangeSnapshot;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.example.risk.service.MoneyManagement.calculateQuantity;
import static com.example.risk.service.PriceCalculator.calculateNotionalSalesPrice;

@Service
public class PositionCalculator {

    public CalculationResult calculate(ExchangeSnapshot snapshot, String wkn, IndividualRisk individualRisk) {
        final ExchangeSnapshot.Quotes foundQuotes = findQuotes(snapshot, wkn);

        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(
                foundQuotes.getRsl(), foundQuotes.getPrice(), snapshot.getExchange().getRsl());

        final MoneyManagement.Parameters parameters = new MoneyManagement.Parameters(
                foundQuotes.getPrice(), notionalSalesPrice, snapshot.getExchange().getTransactionCosts());
        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), parameters);

        return createResult(snapshot, individualRisk, foundQuotes, notionalSalesPrice, quantity);
    }

    public CalculationResult calculate(QueryResult queryResult, String symbol, IndividualRisk individualRisk) {
        final QueryResult.CompanyResult foundQuotes = findQuotes(queryResult, symbol);

        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(
                foundQuotes.getRsl(), foundQuotes.getWeeklyPrice(), queryResult.getExchange().getRsl());

        final MoneyManagement.Parameters parameters = new MoneyManagement.Parameters(
                foundQuotes.getWeeklyPrice(), notionalSalesPrice, queryResult.getExchange().getTransactionCosts());
        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), parameters);

        return createResult(queryResult.getExchange(), individualRisk, foundQuotes, notionalSalesPrice, quantity);
    }

    private ExchangeSnapshot.Quotes findQuotes(ExchangeSnapshot exchangeSnapshot, String wkn) {
        return exchangeSnapshot.getQuotes().stream()
                .filter(data -> data.getWkn().equalsIgnoreCase(wkn))
                .findFirst()
                .orElseThrow();
    }

    private QueryResult.CompanyResult findQuotes(QueryResult queryResult, String symbol) {
        return queryResult.getCompanyResults().stream()
                .filter(company -> company.getSymbol().equalsIgnoreCase(symbol))
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
                .exchangeResult(createExchangeResult(snapshot))
                .positionRisk(individualRisk.calculateIndividualPositionRisk())
                .build();
    }

    private CalculationResult createResult(Exchange exchange, IndividualRisk individualRisk,
                                           QueryResult.CompanyResult company,
                                           BigDecimal notionalSalesPrice, int quantity) {
        return CalculationResult.builder()
                .wkn(company.getSymbol())
                .name(company.getName())
                .price(company.getWeeklyPrice())
                .quantity(quantity)
                .notionalSalesPrice(notionalSalesPrice)
                .transactionCosts(exchange.getTransactionCosts())
                .rsl(exchange.getRsl())
                .exchangeResult(createExchangeResult(exchange))
                .positionRisk(individualRisk.calculateIndividualPositionRisk())
                .build();
    }

    private ExchangeResult createExchangeResult(ExchangeSnapshot snapshot) {
        return new ExchangeResult(snapshot.getExchange().getName(), snapshot.getExchange().getRsl());
    }

    private ExchangeResult createExchangeResult(Exchange exchange) {
        return new ExchangeResult(exchange.getName(), exchange.getRsl());
    }

}