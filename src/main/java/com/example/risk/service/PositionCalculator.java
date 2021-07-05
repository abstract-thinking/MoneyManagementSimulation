package com.example.risk.service;

import com.example.risk.boundary.api.CalculationResult;
import com.example.risk.boundary.api.Exchange;
import com.example.risk.boundary.api.ExchangeResult;
import com.example.risk.boundary.api.QueryResult;
import com.example.risk.data.IndividualRisk;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.example.risk.service.MoneyManagement.calculateQuantity;

@Service
public class PositionCalculator {

    public CalculationResult calculate(QueryResult queryResult, String symbol, IndividualRisk individualRisk,
                                       BigDecimal currentStopPrice) {
        final QueryResult.CompanyResult foundQuotes = findQuotes(queryResult, symbol);

        final MoneyManagement.Parameters parameters = new MoneyManagement.Parameters(
                foundQuotes.getWeeklyPrice(), currentStopPrice, queryResult.getExchange().getTransactionCosts());
        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), parameters);

        return createResult(queryResult.getExchange(), individualRisk, foundQuotes, currentStopPrice, quantity);
    }

    private QueryResult.CompanyResult findQuotes(QueryResult queryResult, String symbol) {
        return queryResult.getCompanyResults().stream()
                .filter(company -> company.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .orElseThrow();
    }

    private CalculationResult createResult(Exchange exchange, IndividualRisk individualRisk,
                                           QueryResult.CompanyResult company,
                                           BigDecimal notionalSalesPrice, int quantity) {
        return CalculationResult.builder()
                .symbol(company.getSymbol())
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

    private ExchangeResult createExchangeResult(Exchange exchange) {
        return new ExchangeResult(exchange.getName(), exchange.getRsl());
    }
}