package com.example.risk.service;

import com.example.risk.boundary.api.CurrentData;
import com.example.risk.boundary.api.CurrentDataResult;
import com.example.risk.boundary.api.Exchange;
import com.example.risk.boundary.api.ExchangeResult;
import com.example.risk.boundary.api.QueryResult;
import com.example.risk.data.Investment;
import com.example.risk.service.finanztreff.ExchangeSnapshot;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.risk.service.PriceCalculator.calculateNotionalSalesPrice;
import static java.util.Comparator.comparingDouble;

@Service
public class CurrentDataProcessor {

    public CurrentDataResult process(ExchangeSnapshot snapshot, List<Investment> investments) {
        final List<CurrentData> currentData = new ArrayList<>();

        investments.forEach(investment ->
                snapshot.getQuotes().forEach(data -> {
                    if (investment.getWkn().equalsIgnoreCase(data.getWkn())) {
                        currentData.add(createCurrentData(investment, snapshot.getExchange(), data));
                    }
                })
        );

        currentData.sort(comparingDouble(CurrentData::getRsl).reversed());

        return new CurrentDataResult(createExchangeResult(snapshot), currentData);
    }

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

    private ExchangeResult createExchangeResult(ExchangeSnapshot snapshot) {
        return new ExchangeResult(snapshot.getExchange().getName(), snapshot.getExchange().getRsl());
    }

    private ExchangeResult createExchangeResult(Exchange exchange) {
        return new ExchangeResult(exchange.getName(), exchange.getRsl());
    }

    private CurrentData createCurrentData(Investment investment, ExchangeSnapshot.Exchange exchange, ExchangeSnapshot.Quotes data) {
        return CurrentData.builder()
                .wkn(data.getWkn())
                .name(data.getName())
                .purchasePrice(investment.getPurchasePrice())
                .currentPrice(data.getPrice())
                .currentStopPrice(calculateNotionalSalesPrice(data.getRsl(), data.getPrice(), exchange.getRsl()))
                .initialStopPrice(investment.getStopPrice())
                .rsl(data.getRsl())
                .build();
    }

    private CurrentData createCurrentData(Investment investment, QueryResult.CompanyResult company) {
        return CurrentData.builder()
                .wkn(company.getSymbol())
                .name(company.getName())
                .purchasePrice(investment.getPurchasePrice())
                .currentPrice(company.getWeeklyPrice())
                // TOOO: currentStopPrice is missing
                .initialStopPrice(investment.getStopPrice())
                .rsl(company.getRsl())
                .build();
    }

}
