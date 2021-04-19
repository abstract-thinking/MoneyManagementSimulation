package com.example.risk.service;

import com.example.risk.boundary.api.ExchangeResult2;
import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendations;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.boundary.api.SalesRecommendations;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import com.example.risk.service.finanztreff.ExchangeSnapshot;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.risk.service.MoneyManagement.calculateQuantity;
import static com.example.risk.service.PriceCalculator.calculateNotionalSalesPrice;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

@Service
public class InvestmentRecommender {

    public SalesRecommendations findSaleRecommendations(ExchangeSnapshot snapshot, List<Investment> investments) {
        List<SaleRecommendation> salesRecommendations = new ArrayList<>();
        investments.forEach(investment ->
                snapshot.getQuotes().stream()
                        .filter(result -> result.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .map(result -> createSellRecommendation(snapshot, result, investment))
                        .filter(SaleRecommendation::shouldSell)
                        .findFirst()
                        .ifPresent(salesRecommendations::add)
        );

        ExchangeResult2 exchangeResult = new ExchangeResult2(snapshot.getExchange().getName(), snapshot.getExchange().getRsl());
        return new SalesRecommendations(exchangeResult, salesRecommendations);
    }

    private SaleRecommendation createSellRecommendation(ExchangeSnapshot exchangeSnapshot, ExchangeSnapshot.Quotes result, Investment investment) {
        return SaleRecommendation.builder()
                .id(investment.getId())
                .wkn(result.getWkn())
                .name(result.getName())
                .rsl(result.getRsl())
                .price(result.getPrice())
                .notionalSalesPrice(investment.getStopPrice())
                .shouldSellByStopPrice(isCurrentPriceLowerThanStopPrice(result, investment))
                .shouldSellByRslComparison(isCompanyRslLowerThanExchangeRsl(exchangeSnapshot, result))
                .build();
    }

    private boolean isCurrentPriceLowerThanStopPrice(ExchangeSnapshot.Quotes result, Investment investment) {
        return result.getPrice().min(investment.getStopPrice()).equals(result.getPrice());
    }

    private boolean isCompanyRslLowerThanExchangeRsl(ExchangeSnapshot snapshot, ExchangeSnapshot.Quotes company) {
        return company.getRsl() < snapshot.getExchange().getRsl();
    }

    public PurchaseRecommendations findPurchaseRecommendations(ExchangeSnapshot snapshot, IndividualRisk individualRisk) {
        List<PurchaseRecommendation> purchaseRecommendations = snapshot.getQuotes().stream()
                .filter(result -> result.getRsl() > snapshot.getExchange().getRsl())
                .sorted(comparingDouble(ExchangeSnapshot.Quotes::getRsl).reversed())
                .limit(7)
                .map(result -> createBuyRecommendation(snapshot, result, individualRisk))
                .collect(toList());

        ExchangeResult2 exchangeResult = new ExchangeResult2(snapshot.getExchange().getName(), snapshot.getExchange().getRsl());
        return new PurchaseRecommendations(exchangeResult, purchaseRecommendations);
    }

    private PurchaseRecommendation createBuyRecommendation(ExchangeSnapshot snapshot, ExchangeSnapshot.Quotes result, IndividualRisk individualRisk) {
        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(result.getRsl(), result.getPrice(),
                snapshot.getExchange().getRsl());

        final MoneyManagement.Parameters purchaseParameters = new MoneyManagement.Parameters(
                result.getPrice(), notionalSalesPrice, snapshot.getExchange().getTransactionCosts());
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
