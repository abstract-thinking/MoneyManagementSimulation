package com.example.risk.control.management.caclulate;

import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendationMetadata;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.boundary.api.SalesRecommendationMetadata;
import com.example.risk.converter.ExchangeData;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.risk.control.management.caclulate.MoneyManagement.calculateQuantity;
import static com.example.risk.control.management.caclulate.PriceCalculator.calculateNotionalSalesPrice;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
public class InvestmentRecommender {

    private final ExchangeSnapshot exchangeSnapshot;

    public SalesRecommendationMetadata findSaleRecommendations(List<Investment> investments) {
        List<SaleRecommendation> salesRecommendations = new ArrayList<>();
        investments.forEach(investment ->
                exchangeSnapshot.getExchangeData().stream()
                        .filter(result -> result.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .map(result -> createSellRecommendation(result, investment))
                        .filter(SaleRecommendation::shouldSell)
                        .findFirst()
                        .ifPresent(salesRecommendations::add));

        return new SalesRecommendationMetadata(exchangeSnapshot.getExchangeName(),
                exchangeSnapshot.getExchangeRsl(), salesRecommendations);
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
        return result.getRsl() < exchangeSnapshot.getExchangeRsl();
    }

    public PurchaseRecommendationMetadata findPurchaseRecommendations(IndividualRisk individualRisk) {
        List<PurchaseRecommendation> purchaseRecommendations = exchangeSnapshot.getExchangeData().stream()
                .filter(result -> result.getRsl() > exchangeSnapshot.getExchangeRsl())
                .sorted(comparingDouble(ExchangeData::getRsl).reversed())
                .limit(7)
                .map(result -> createBuyRecommendation(result, individualRisk))
                .collect(toList());

        return new PurchaseRecommendationMetadata(exchangeSnapshot.getExchangeName(),
                exchangeSnapshot.getExchangeRsl(), purchaseRecommendations);
    }

    private PurchaseRecommendation createBuyRecommendation(ExchangeData result, IndividualRisk individualRisk) {
        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(result.getRsl(), result.getPrice(),
                exchangeSnapshot.getExchangeRsl());

        Investment possibleInvestment = Investment.builder()
                .purchasePrice(result.getPrice())
                .stopPrice(notionalSalesPrice)
                .transactionCosts(exchangeSnapshot.getExchangeTransactionCosts())
                .build();

        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), possibleInvestment);

        return createPurchaseRecommendation(result, notionalSalesPrice, quantity);
    }

    private PurchaseRecommendation createPurchaseRecommendation(ExchangeData result, BigDecimal notionalSalesPrice, int quantity) {
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
