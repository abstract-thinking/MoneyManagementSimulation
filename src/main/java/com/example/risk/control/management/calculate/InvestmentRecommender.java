package com.example.risk.control.management.calculate;

import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendationMetadata;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.boundary.api.SalesRecommendationMetadata;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import com.example.risk.service.finanztreff.ExchangeSnapshot;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.risk.control.management.calculate.MoneyManagement.calculateQuantity;
import static com.example.risk.control.management.calculate.PriceCalculator.calculateNotionalSalesPrice;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
public class InvestmentRecommender {

    private final ExchangeSnapshot exchangeSnapshot;

    public SalesRecommendationMetadata findSaleRecommendations(List<Investment> investments) {
        List<SaleRecommendation> salesRecommendations = new ArrayList<>();
        investments.forEach(investment ->
                exchangeSnapshot.getQuotes().stream()
                        .filter(result -> result.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .map(result -> createSellRecommendation(result, investment))
                        .filter(SaleRecommendation::shouldSell)
                        .findFirst()
                        .ifPresent(salesRecommendations::add)
        );

        return new SalesRecommendationMetadata(exchangeSnapshot.getName(), exchangeSnapshot.getRsl(), salesRecommendations);
    }

    private SaleRecommendation createSellRecommendation(ExchangeSnapshot.Quotes result, Investment investment) {
        return SaleRecommendation.builder()
                .id(investment.getId())
                .wkn(result.getWkn())
                .name(result.getName())
                .rsl(result.getRsl())
                .price(result.getPrice())
                .notionalSalesPrice(investment.getStopPrice())
                .shouldSellByStopPrice(isCurrentPriceLowerThanStopPrice(result, investment))
                .shouldSellByRslComparison(isCompanyRslLowerThanExchangeRsl(result))
                .build();
    }

    private boolean isCurrentPriceLowerThanStopPrice(ExchangeSnapshot.Quotes result, Investment investment) {
        return result.getPrice().min(investment.getStopPrice()).equals(result.getPrice());
    }

    private boolean isCompanyRslLowerThanExchangeRsl(ExchangeSnapshot.Quotes result) {
        return result.getRsl() < exchangeSnapshot.getRsl();
    }

    public PurchaseRecommendationMetadata findPurchaseRecommendations(IndividualRisk individualRisk) {
        List<PurchaseRecommendation> purchaseRecommendations = exchangeSnapshot.getQuotes().stream()
                .filter(result -> result.getRsl() > exchangeSnapshot.getRsl())
                .sorted(comparingDouble(ExchangeSnapshot.Quotes::getRsl).reversed())
                .limit(7)
                .map(result -> createBuyRecommendation(result, individualRisk))
                .collect(toList());

        return new PurchaseRecommendationMetadata(exchangeSnapshot.getName(),
                exchangeSnapshot.getRsl(), purchaseRecommendations);
    }

    private PurchaseRecommendation createBuyRecommendation(ExchangeSnapshot.Quotes result, IndividualRisk individualRisk) {
        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(result.getRsl(), result.getPrice(),
                exchangeSnapshot.getRsl());

        final MoneyManagement.Parameters purchaseParameters = new MoneyManagement.Parameters(
                result.getPrice(), notionalSalesPrice, exchangeSnapshot.getTransactionCosts());
        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), purchaseParameters);

        return createPurchaseRecommendation(result, notionalSalesPrice, quantity);
    }

    private PurchaseRecommendation createPurchaseRecommendation(ExchangeSnapshot.Quotes result, BigDecimal notionalSalesPrice, int quantity) {
        return PurchaseRecommendation.builder()
                .name(result.getName())
                .rsl(result.getRsl())
                .wkn(result.getWkn())
                .price(result.getPrice())
                .notionalSalesPrice(notionalSalesPrice)
                .quantity(quantity)
                .build();
    }
}
