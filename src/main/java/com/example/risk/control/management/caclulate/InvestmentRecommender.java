package com.example.risk.control.management.caclulate;

import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.converter.DecisionRowConverter;
import com.example.risk.converter.ExchangeResult;
import com.example.risk.data.Investment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.risk.control.management.caclulate.MoneyManagement.calculateQuantity;
import static com.example.risk.control.management.caclulate.PriceCalculator.calculateNotionalSalesPrice;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

@Slf4j
@AllArgsConstructor
@Component
public class InvestmentRecommender {

    public static final BigDecimal EXCHANGE_TRANSACTION_COSTS = BigDecimal.valueOf(35.50);

    private static final String EXCHANGE_NAME = "NASDAQ 100";

    private final DecisionRowConverter converter;

    public List<SaleRecommendation> getSaleRecommendations(RiskManagementCalculator riskManagementCalculator) {
        List<ExchangeResult> results = converter.fetchTable();
        final double exchangeRsl = findExchangeRsl(results);

        List<SaleRecommendation> saleRecommendations = new ArrayList<>();
        riskManagementCalculator.getInvestments().forEach(investment ->
                results.stream()
                        .filter(result -> result.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .map(result -> createSellRecommendation(result, exchangeRsl, investment))
                        .filter(SaleRecommendation::shouldSell)
                        .findFirst()
                        .ifPresent(saleRecommendations::add));

        return saleRecommendations;
    }

    private double findExchangeRsl(List<ExchangeResult> results) {
        return results.stream()
                .filter(result -> result.getName().equals(EXCHANGE_NAME))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

    private SaleRecommendation createSellRecommendation(ExchangeResult result, double exchangeRsl, Investment investment) {
        return SaleRecommendation.builder()
                .wkn(result.getWkn())
                .company(result.getName())
                .companyRsl(result.getRsl())
                .exchange(EXCHANGE_NAME)
                .exchangeRsl(exchangeRsl)
                .price(result.getPrice())
                .initialNotionalSalesPrice(investment.getInitialNotionalSalesPrice())
                .shouldSellByFallingBelowTheLimit(isCurrentPriceLowerThanInitialNotionalSalesPrice(investment, result))
                .shouldSellByRslComparison(isCompanyRslLowerThanExchangeRsl(exchangeRsl, result))
                .build();
    }

    private boolean isCurrentPriceLowerThanInitialNotionalSalesPrice(Investment investment, ExchangeResult result) {
        return investment.getInitialNotionalSalesPrice().compareTo(result.getPrice()) >= 0;
    }

    private boolean isCompanyRslLowerThanExchangeRsl(double exchangeRsl, ExchangeResult result) {
        return exchangeRsl > result.getRsl();
    }

    public List<PurchaseRecommendation> getPurchaseRecommendations() {
        List<ExchangeResult> results = converter.fetchTable();
        final double exchangeRsl = findExchangeRsl(results);

        return results.stream()
                .filter(result -> result.getRsl() > exchangeRsl)
                .sorted(comparingDouble(ExchangeResult::getRsl).reversed())
                .map(result -> createBuyRecommendation(result, exchangeRsl))
                .collect(toList());
    }

    public List<PurchaseRecommendation> getPurchaseRecommendations(RiskManagementCalculator riskManagementCalculator) {
        List<ExchangeResult> results = converter.fetchTable();
        final double exchangeRsl = findExchangeRsl(results);

        return results.stream()
                .filter(result -> result.getRsl() > exchangeRsl)
                .sorted(comparingDouble(ExchangeResult::getRsl).reversed())
                .map(result -> createBuyRecommendation(result, exchangeRsl, riskManagementCalculator))
                .collect(toList());
    }

    private PurchaseRecommendation createBuyRecommendation(ExchangeResult result, double exchangeRsl) {
        return PurchaseRecommendation.builder()
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

    private PurchaseRecommendation createBuyRecommendation(ExchangeResult result, double exchangeRsl, RiskManagementCalculator riskManagementCalculator) {
        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(result.getRsl(), result.getPrice(), exchangeRsl);

        Investment possibleInvestment = Investment.builder()
                .purchasePrice(result.getPrice())
                .currentNotionalSalesPrice(notionalSalesPrice)
                .transactionCosts(EXCHANGE_TRANSACTION_COSTS)
                .build();

        int quantity = calculateQuantity(riskManagementCalculator.calculatePositionRisk(), possibleInvestment);

        return PurchaseRecommendation.builder()
                .name(result.getName())
                .rsl(result.getRsl())
                .wkn(result.getWkn())
                .vola30Day(result.getVola30Day())
                .price(result.getPrice())
                .exchange(EXCHANGE_NAME)
                .exchangeRsl(exchangeRsl)
                .notionalSalesPrice(notionalSalesPrice)
                .quantity(quantity)
                .build();
    }
}
