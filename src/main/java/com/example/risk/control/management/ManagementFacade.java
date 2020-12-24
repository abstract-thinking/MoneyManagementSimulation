package com.example.risk.control.management;


import com.example.risk.converter.DecisionRowConverter;
import com.example.risk.converter.ExchangeResult;
import com.example.risk.data.Investment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.risk.control.management.PriceCalculator.calculateNotionalSalesPrice;

@Slf4j
@Component
public class ManagementFacade {

    private static final String EXCHANGE_NAME = "NASDAQ 100";

    private final DecisionRowConverter converter;

    public ManagementFacade(DecisionRowConverter converter) {
        this.converter = converter;
    }

    public List<Investment> updateNotionalSalesPrice(List<Investment> investments) {
        List<ExchangeResult> exchangeResults = converter.fetchTable();
        final double exchangeRsl = findExchangeRsl(exchangeResults);

        investments.forEach(
                investment -> exchangeResults.stream()
                        .filter(row -> row.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .findFirst()
                        .ifPresent(result -> investment.setUpdatedNotionalSalesPrice(
                                calculateNotionalSalesPrice(result.getRsl(), result.getPrice(), exchangeRsl)))
        );

        return investments;
    }

    private double findExchangeRsl(List<ExchangeResult> rows) {
        return rows.stream()
                .filter(row -> EXCHANGE_NAME.equals(row.getName()))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

}

