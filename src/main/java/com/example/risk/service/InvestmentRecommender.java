package com.example.risk.service;

import com.example.risk.boundary.api.ExchangeResult;
import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendations;
import com.example.risk.boundary.api.QueryResult;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.boundary.api.SalesRecommendations;
import com.example.risk.control.management.Exchange;
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

        return new SalesRecommendations(createExchangeResult(snapshot), salesRecommendations);
    }

    public SalesRecommendations findSaleRecommendations(QueryResult queryResult, List<Investment> investments) {
        List<SaleRecommendation> salesRecommendations = new ArrayList<>();
        investments.forEach(investment ->
                queryResult.getCompanyResults().stream()
                        .filter(company -> company.getSymbol().equalsIgnoreCase(investment.getSymbol()))
                        .map(result -> createSellRecommendation(queryResult.getExchange(), result, investment))
                        .filter(SaleRecommendation::shouldSell)
                        .findFirst()
                        .ifPresent(salesRecommendations::add)
        );

        return new SalesRecommendations(createExchangeResult(queryResult.getExchange()), salesRecommendations);
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

    private SaleRecommendation createSellRecommendation(Exchange exchange,
                                                        QueryResult.CompanyResult company,
                                                        Investment investment) {
        return SaleRecommendation.builder()
                .id(investment.getId())
                .wkn(company.getSymbol())
                .name(company.getName())
                .rsl(company.getRsl())
                .price(company.getWeeklyPrice())
                .notionalSalesPrice(investment.getStopPrice())
                .shouldSellByStopPrice(isCurrentPriceLowerThanStopPrice(company, investment))
                .shouldSellByRslComparison(isCompanyRslLowerThanExchangeRsl(exchange, company))
                .build();
    }

    private boolean isCurrentPriceLowerThanStopPrice(ExchangeSnapshot.Quotes result, Investment investment) {
        return result.getPrice().min(investment.getStopPrice()).equals(result.getPrice());
    }

    private boolean isCurrentPriceLowerThanStopPrice(QueryResult.CompanyResult result, Investment investment) {
        return result.getStopPrice().min(investment.getStopPrice()).equals(result.getStopPrice());
    }

    private boolean isCompanyRslLowerThanExchangeRsl(ExchangeSnapshot snapshot, ExchangeSnapshot.Quotes company) {
        return company.getRsl() < snapshot.getExchange().getRsl();
    }

    private boolean isCompanyRslLowerThanExchangeRsl(Exchange exchange, QueryResult.CompanyResult company) {
        return company.getRsl() < exchange.getRsl();
    }

    public PurchaseRecommendations findPurchaseRecommendations(ExchangeSnapshot snapshot, IndividualRisk individualRisk) {
        List<PurchaseRecommendation> purchaseRecommendations = snapshot.getQuotes().stream()
                .filter(result -> result.getRsl() > snapshot.getExchange().getRsl())
                .sorted(comparingDouble(ExchangeSnapshot.Quotes::getRsl).reversed())
                .limit(7)
                .map(result -> createBuyRecommendation(snapshot, result, individualRisk))
                .collect(toList());

        return new PurchaseRecommendations(createExchangeResult(snapshot), purchaseRecommendations);
    }

    public PurchaseRecommendations findPurchaseRecommendations(QueryResult queryResult, IndividualRisk individualRisk) {
        List<PurchaseRecommendation> purchaseRecommendations = queryResult.getCompanyResults().stream()
                .filter(result -> result.getRsl() > queryResult.getExchange().getRsl())
                .sorted(comparingDouble(QueryResult.CompanyResult::getRsl).reversed())
                .limit(7)
                .map(company -> createBuyRecommendation(queryResult.getExchange(), company, individualRisk))
                .collect(toList());

        return new PurchaseRecommendations(createExchangeResult(queryResult.getExchange()), purchaseRecommendations);
    }

    private ExchangeResult createExchangeResult(ExchangeSnapshot snapshot) {
        return new ExchangeResult(snapshot.getExchange().getName(), snapshot.getExchange().getRsl());
    }

    private ExchangeResult createExchangeResult(Exchange exchange) {
        return new ExchangeResult(exchange.getName(), exchange.getRsl());
    }

    private PurchaseRecommendation createBuyRecommendation(ExchangeSnapshot snapshot, ExchangeSnapshot.Quotes result, IndividualRisk individualRisk) {
        final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(result.getRsl(), result.getPrice(),
                snapshot.getExchange().getRsl());

        final MoneyManagement.Parameters parameters = new MoneyManagement.Parameters(
                result.getPrice(), notionalSalesPrice, snapshot.getExchange().getTransactionCosts());
        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), parameters);

        return createPurchaseRecommendation(result, notionalSalesPrice, quantity);
    }

    private PurchaseRecommendation createBuyRecommendation(Exchange exchange,
                                                           QueryResult.CompanyResult company,
                                                           IndividualRisk individualRisk) {

        final MoneyManagement.Parameters parameters = new MoneyManagement.Parameters(
                company.getWeeklyPrice(), company.getStopPrice(), exchange.getTransactionCosts());
        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), parameters);

        return createPurchaseRecommendation(company, quantity);
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

    private PurchaseRecommendation createPurchaseRecommendation(QueryResult.CompanyResult company, int quantity) {
        return PurchaseRecommendation.builder()
                .wkn(company.getSymbol())
                .name(company.getName())
                .rsl(company.getRsl())
                .price(company.getWeeklyPrice())
                .notionalSalesPrice(company.getStopPrice())
                .quantity(quantity)
                .build();
    }

}
