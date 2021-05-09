package com.example.risk.service;

import com.example.risk.boundary.api.Exchange;
import com.example.risk.boundary.api.ExchangeResult;
import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendations;
import com.example.risk.boundary.api.QueryResult;
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
                        .filter(company -> company.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .map(company -> createSellRecommendation(snapshot, company,
                                currentNotionalSalesPrice(snapshot, company, investment)))
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
                        .map(company -> createSellRecommendation(queryResult.getExchange(), company,
                                currentNotionalSalesPrice(queryResult.getCurrent(), investment)))
                        .filter(SaleRecommendation::shouldSell)
                        .findFirst()
                        .ifPresent(salesRecommendations::add)
        );

        return new SalesRecommendations(createExchangeResult(queryResult.getExchange()), salesRecommendations);
    }

    private BigDecimal currentNotionalSalesPrice(ExchangeSnapshot exchangeSnapshot, ExchangeSnapshot.Quotes quotes, Investment investment) {
        BigDecimal calculatedNotionalSalesPrice =
                calculateNotionalSalesPrice(quotes.getRsl(), quotes.getPrice(), exchangeSnapshot.getExchange().getRsl());

        return investment.getStopPrice().max(calculatedNotionalSalesPrice);
    }

    private BigDecimal currentNotionalSalesPrice(QueryResult.CompanyResult company, Investment investment) {
        return investment.getStopPrice().max(company.getStopPrice());
    }

    private SaleRecommendation createSellRecommendation(
            ExchangeSnapshot exchangeSnapshot, ExchangeSnapshot.Quotes company, BigDecimal currentNotionalSalesPrice) {

        return SaleRecommendation.builder()
                .wkn(company.getWkn())
                .name(company.getName())
                .rsl(company.getRsl())
                .price(company.getPrice())
                .notionalSalesPrice(currentNotionalSalesPrice)
                .shouldSellByStopPrice(isCurrentPriceLowerThanStopPrice(company, currentNotionalSalesPrice))
                .shouldSellByRslComparison(isCompanyRslLowerThanExchangeRsl(exchangeSnapshot, company))
                .build();
    }

    private SaleRecommendation createSellRecommendation(Exchange exchange, QueryResult.CompanyResult company,
                                                        BigDecimal currentNotionalSalesPrice) {
        return SaleRecommendation.builder()
                .wkn(company.getSymbol())
                .name(company.getName())
                .rsl(company.getRsl())
                .price(company.getWeeklyPrice())
                .notionalSalesPrice(currentNotionalSalesPrice)
                .shouldSellByStopPrice(isCurrentPriceLowerThanStopPrice(company, currentNotionalSalesPrice))
                .shouldSellByRslComparison(isCompanyRslLowerThanExchangeRsl(exchange, company))
                .build();
    }

    private boolean isCurrentPriceLowerThanStopPrice(ExchangeSnapshot.Quotes quotes, BigDecimal currentStopPrice) {
        return quotes.getPrice().min(currentStopPrice).equals(quotes.getPrice());
    }

    private boolean isCurrentPriceLowerThanStopPrice(QueryResult.CompanyResult quotes, BigDecimal currentStopPrice) {
        return quotes.getStopPrice().min(currentStopPrice).equals(quotes.getStopPrice());
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
