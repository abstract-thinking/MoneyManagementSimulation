package com.example.risk.service;

import com.example.risk.boundary.api.CurrentData;
import com.example.risk.boundary.api.CurrentDataResult;
import com.example.risk.boundary.api.Exchange;
import com.example.risk.boundary.api.ExchangeResult;
import com.example.risk.boundary.api.QueryResult;
import com.example.risk.data.Investment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparingDouble;

@Service
public class CurrentDataProcessor {

    public CurrentDataResult process(QueryResult queryResult, List<Investment> investments) {
        final List<CurrentData> currentData = new ArrayList<>();

        investments.forEach(investment ->
                queryResult.getCompanyResults().forEach(company -> {
                    if (investment.getSymbol().equalsIgnoreCase(company.getSymbol())) {
                        currentData.add(createCurrentData(investment, company));
                    }
                })
        );

        currentData.sort(comparingDouble(CurrentData::getRsl).reversed());

        return new CurrentDataResult(createExchangeResult(queryResult.getExchange()), currentData);
    }

    private ExchangeResult createExchangeResult(Exchange exchange) {
        return new ExchangeResult(exchange.getName(), exchange.getRsl());
    }

    private CurrentData createCurrentData(Investment investment, QueryResult.CompanyResult company) {
        return CurrentData.builder()
                .symbol(company.getSymbol())
                .name(company.getName())
                .purchasePrice(investment.getPurchasePrice())
                .currentPrice(company.getWeeklyPrice())
                .initialStopPrice(investment.getStopPrice())
                .currentStopPrice(company.getCurrentStopPrice())
                .rsl(company.getRsl())
                .build();
    }
}
