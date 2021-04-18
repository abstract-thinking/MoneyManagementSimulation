package com.example.risk;

import com.example.risk.service.tradier.Quote;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.DoubleEMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.UP;

@Slf4j
public class RslExchangeManager {

    private static final int RSL_WEEKS = 27;

    public ExchangeResult doIt(String exchange) throws ExecutionException, InterruptedException {
        // List<Company> companies = fetchSymbols(exchange);

        Map<Company, CompletableFuture<List<Quote>>> futures = new HashMap<>();
        // for (Company company : companies) {
        //    futures.put(company, fetchWeeklyQuotes(company.getSymbol()));
        // }

        ExchangeResult result = new ExchangeResult(exchange);
        for (Map.Entry<Company, CompletableFuture<List<Quote>>> future : futures.entrySet()) {
            result.add(buildCompanyResult(future.getKey(), future.getValue().get()));
        }

        result.calculateRsl();

        return result;
    }

    private ExchangeResult.CompanyResult buildCompanyResult(Company company, List<Quote> quotes) {
        final Quote lastQuote = quotes.get(quotes.size() - 1);
        return ExchangeResult.CompanyResult.builder()
                .company(company)
                .rsl(calculateRelativeStrengthLevy(getRslWeekQuotes(quotes)))
                .stopPrice(calculateStopPrice(quotes))
                .date(lastQuote.getDate())
                .weeklyPrice(lastQuote.getClose())
                .build();
    }

    private double calculateRelativeStrengthLevy(List<Quote> rslQuotes) {
        assert rslQuotes.size() == RSL_WEEKS;
        final BigDecimal average = sum(rslQuotes).divide(valueOf(rslQuotes.size()), 4, UP);

        return rslQuotes.get(rslQuotes.size() - 1).getClose().divide(average, 4, UP).doubleValue();
    }

    private List<Quote> getRslWeekQuotes(List<Quote> quotes) {
        return quotes.subList(quotes.size() - RSL_WEEKS, quotes.size());
    }

    private BigDecimal sum(List<Quote> quotes) {
        return BigDecimal.valueOf(quotes.stream()
                .map(Quote::getClose)
                .mapToDouble(BigDecimal::doubleValue)
                .sum());
    }

    private BigDecimal calculateStopPrice(List<Quote> quotes) {
        return BigDecimal.valueOf(calculateDoubleEMAIndicator(quotes).getValue(quotes.size() - 1).doubleValue());
    }

    private DoubleEMAIndicator calculateDoubleEMAIndicator(List<Quote> weeklyQuotes) {
        final BarSeries series = new BaseBarSeries();
        weeklyQuotes.forEach(quote ->
                series.addBar(quote.getDate().atStartOfDay(ZoneId.systemDefault()),
                        quote.getOpen(), quote.getHigh(), quote.getLow(), quote.getClose(), quote.getVolume())
        );

        return new DoubleEMAIndicator(new ClosePriceIndicator(series), RSL_WEEKS);
    }

}
