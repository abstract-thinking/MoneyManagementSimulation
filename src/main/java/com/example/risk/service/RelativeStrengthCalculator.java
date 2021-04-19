package com.example.risk.service;

import com.example.risk.boundary.api.ExchangeResult;
import com.example.risk.service.tradier.Quote;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.UP;

@Service
public class RelativeStrengthCalculator {

    private static final int RSL_WEEKS = 27;

    public double calculateRelativeStrengthLevy(List<Quote> quotes) {
        List<Quote> rslQuotes = cutOffTail(quotes);
        assert rslQuotes.size() == RSL_WEEKS;

        final BigDecimal average = sumQuotes(rslQuotes).divide(valueOf(rslQuotes.size()), 4, UP);

        return rslQuotes.get(rslQuotes.size() - 1).getClose().divide(average, 4, UP).doubleValue();
    }

    private List<Quote> cutOffTail(List<Quote> quotes) {
        return quotes.subList(quotes.size() - RSL_WEEKS, quotes.size());
    }

    private BigDecimal sumQuotes(List<Quote> quotes) {
        return BigDecimal.valueOf(quotes.stream()
                .map(Quote::getClose)
                .mapToDouble(BigDecimal::doubleValue)
                .sum());
    }

    public double calculateExchangeRsl(List<ExchangeResult.CompanyResult> companyResults) {
        return sum(companyResults) / companyResults.size();
    }

    private double sum(List<ExchangeResult.CompanyResult> companyResults) {
        return companyResults.stream()
                .map(ExchangeResult.CompanyResult::getRsl)
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}
