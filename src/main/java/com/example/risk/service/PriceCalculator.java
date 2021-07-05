package com.example.risk.service;

import com.example.risk.service.tradier.Quote;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.DoubleEMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;

@Service
public class PriceCalculator {

    private static final int RSL_WEEKS = 27;

    public BigDecimal calculateStopPrice(List<Quote> quotes) {
        if (quotes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(calculateDoubleEMAIndicator(quotes).getValue(quotes.size() - 1).doubleValue());
    }

    private static DoubleEMAIndicator calculateDoubleEMAIndicator(List<Quote> weeklyQuotes) {
        final BarSeries series = new BaseBarSeries();
        weeklyQuotes.forEach(quote ->
                series.addBar(quote.getDate().atStartOfDay(ZoneId.systemDefault()),
                        quote.getOpen(), quote.getHigh(), quote.getLow(), quote.getClose(), quote.getVolume())
        );

        return new DoubleEMAIndicator(new ClosePriceIndicator(series), RSL_WEEKS);
    }

}
