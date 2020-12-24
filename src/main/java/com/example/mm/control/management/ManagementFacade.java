package com.example.mm.control.management;


import com.example.mm.control.invest.ExchangeResult;
import com.example.mm.converter.DecisionRowConverter;
import com.example.mm.data.Investment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.mm.control.management.PriceCalculator.calculateNotionalSalesPrice;

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

