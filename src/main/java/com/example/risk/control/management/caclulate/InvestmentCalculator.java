package com.example.risk.control.management.caclulate;

import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendationMetadata;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.boundary.api.SalesRecommendationMetadata;
import com.example.risk.converter.ExchangeData;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.risk.control.management.caclulate.MoneyManagement.calculateQuantity;
import static com.example.risk.control.management.caclulate.PriceCalculator.calculateNotionalSalesPrice;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

public class InvestmentCalculator extends Calculator {

    public InvestmentCalculator(List<ExchangeData> exchangeData) {
        super(exchangeData);
    }

    public SalesRecommendationMetadata getSaleRecommendations(List<Investment> investments) {
        List<SaleRecommendation> salesRecommendations = new ArrayList<>();
        investments.forEach(investment ->
                exchangeData.stream()
                        .filter(result -> result.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .map(result -> createSellRecommendation(result, investment))
                        .filter(SaleRecommendation::shouldSell)
                        .findFirst()
                        .ifPresent(salesRecommendations::add));

        return new SalesRecommendationMetadata(EXCHANGE_NAME, exchangeRsl, salesRecommendations);
    }

    private SaleRecommendation createSellRecommendation(ExchangeData result, Investment investment) {
        return SaleRecommendation.builder()
                .id(investment.getId())
                .wkn(result.getWkn())
                .name(result.getName())
                .rsl(result.getRsl())
                .price(result.getPrice())
                .notionalSalesPrice(investment.getStopPrice())
                .shouldSellByStopPrice(isCurrentPriceLowerThanStopPrice(investment, result))
                .shouldSellByRslComparison(isCompanyRslLowerThanExchangeRsl(result))
                .build();
    }

    private boolean isCurrentPriceLowerThanStopPrice(Investment investment, ExchangeData result) {
        return result.getPrice().min(investment.getStopPrice()).equals(result.getPrice());
    }

    private boolean isCompanyRslLowerThanExchangeRsl(ExchangeData result) {
        return result.getRsl() < exchangeRsl;
    }

    public PurchaseRecommendationMetadata getPurchaseRecommendations(IndividualRisk individualRisk) {
        List<PurchaseRecommendation> purchaseRecommendations = exchangeData.stream()
                .filter(result -> result.getRsl() > exchangeRsl)
                .sorted(comparingDouble(ExchangeData::getRsl).reversed())
                .limit(7)
                .map(result -> createBuyRecommendation(result, individualRisk))
                .collect(toList());

        return new PurchaseRecommendationMetadata(EXCHANGE_NAME, exchangeRsl, purchaseRecommendations);
    }

    private PurchaseRecommendation createBuyRecommendation(ExchangeData result, IndividualRisk individualRisk) {
        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(result.getRsl(), result.getPrice(), exchangeRsl);

        Investment possibleInvestment = Investment.builder()
                .purchasePrice(result.getPrice())
                .stopPrice(notionalSalesPrice)
                .transactionCosts(EXCHANGE_TRANSACTION_COSTS)
                .build();

        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), possibleInvestment);

        return PurchaseRecommendation.builder()
                .name(result.getName())
                .rsl(result.getRsl())
                .wkn(result.getWkn())
                .vola30Day(result.getVola30Day())
                .price(result.getPrice())
                .notionalSalesPrice(notionalSalesPrice)
                .quantity(quantity)
                .build();
    }
}
