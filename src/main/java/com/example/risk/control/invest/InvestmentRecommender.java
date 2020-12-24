package com.example.risk.control.invest;

import com.example.risk.boundary.api.BuyRecommendation;
import com.example.risk.boundary.api.SellRecommendation;
import com.example.risk.control.management.RiskManagement;
import com.example.risk.converter.DecisionRowConverter;
import com.example.risk.converter.ExchangeResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.example.risk.control.management.PriceCalculator.calculateNotionalSalesPrice;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

@Slf4j
@AllArgsConstructor
@Component
public class InvestmentRecommender {

    private static final String EXCHANGE_NAME = "NASDAQ 100";

    private final DecisionRowConverter converter;

    public List<SellRecommendation> getSellRecommendations(RiskManagement riskManagement) {
        List<ExchangeResult> results = converter.fetchTable();
        final double exchangeRsl = findExchangeRsl(results);

        List<SellRecommendation> sellRecommendations = new ArrayList<>();
        riskManagement.getInvestments().forEach(investment ->
                results.stream()
                        .filter(result -> result.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .map(result -> createSellRecommendation(result, exchangeRsl))
                        .filter(sellRecommendation -> investment.shouldSell(riskManagement.calculatePositionRisk(), sellRecommendation))
                        .findFirst()
                        .ifPresent(sellRecommendations::add));

        return sellRecommendations;
    }

    private double findExchangeRsl(List<ExchangeResult> results) {
        return results.stream()
                .filter(result -> result.getName().equals(EXCHANGE_NAME))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

    private SellRecommendation createSellRecommendation(ExchangeResult result, double exchangeRsl) {
        return SellRecommendation.builder()
                .wkn(result.getWkn())
                .company(result.getName())
                .companyRsl(result.getRsl())
                .exchange(EXCHANGE_NAME)
                .exchangeRsl(exchangeRsl)
                .price(result.getPrice())
                .build();
    }

    public List<BuyRecommendation> getBuyRecommendations() {
        List<ExchangeResult> results = converter.fetchTable();
        final double exchangeRsl = findExchangeRsl(results);
        return results.stream()
                .filter(result -> result.getRsl() > exchangeRsl)
                .sorted(comparingDouble(ExchangeResult::getRsl).reversed())
                .map(result -> createBuyRecommendation(result, exchangeRsl))
                .collect(toList());
    }

    private BuyRecommendation createBuyRecommendation(ExchangeResult result, double exchangeRsl) {
        return BuyRecommendation.builder()
                .name(result.getName())
                .rsl(result.getRsl())
                .wkn(result.getWkn())
                .vola30Day(result.getVola30Day())
                .price(result.getPrice())
                .exchange(EXCHANGE_NAME)
                .exchangeRsl(exchangeRsl)
                .notionalSalesPrice(calculateNotionalSalesPrice(result.getRsl(), result.getPrice(), exchangeRsl))
                .build();
    }
}
