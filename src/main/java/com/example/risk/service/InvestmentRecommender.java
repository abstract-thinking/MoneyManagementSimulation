package com.example.risk.service;

import com.example.risk.boundary.api.Exchange;
import com.example.risk.boundary.api.ExchangeResult;
import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendations;
import com.example.risk.boundary.api.QueryResult;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.boundary.api.SaleRecommendations;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.risk.service.MoneyManagement.calculateQuantity;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

@Service
public class InvestmentRecommender {

    public SaleRecommendations findSaleRecommendations(QueryResult result, List<Investment> investments) {
        List<SaleRecommendation> salesRecommendations = new ArrayList<>();
        investments.forEach(investment ->
                result.getCompanyResults().stream()
                        .filter(company -> company.getSymbol().equalsIgnoreCase(investment.getSymbol()))
                        .filter(company -> shouldSell(result.getExchange(), company,
                                currentNotionalSalesPrice(result.getCurrent(), investment)))
                        .map(company -> createSellRecommendation(result.getExchange(), company,
                                currentNotionalSalesPrice(result.getCurrent(), investment)))
                        .findFirst()
                        .ifPresent(salesRecommendations::add)
        );

        return new SaleRecommendations(createExchangeResult(result.getExchange()), salesRecommendations);
    }

    private boolean shouldSell(Exchange exchange, QueryResult.CompanyResult company, BigDecimal currentNotionalSalesPrice) {
        return isCompanyRslLowerThanExchangeRsl(exchange, company) ||
                isCurrentPriceLowerThanStopPrice(company, currentNotionalSalesPrice);
    }

    private BigDecimal currentNotionalSalesPrice(QueryResult.CompanyResult company, Investment investment) {
        return investment.getStopPrice().max(company.getCurrentStopPrice());
    }

    private SaleRecommendation createSellRecommendation(Exchange exchange, QueryResult.CompanyResult company,
                                                        BigDecimal currentNotionalSalesPrice) {
        return SaleRecommendation.builder()
                .symbol(company.getSymbol())
                .name(company.getName())
                .rsl(company.getRsl())
                .price(company.getWeeklyPrice())
                .notionalSalesPrice(currentNotionalSalesPrice)
                .shouldSellByRslComparison(isCompanyRslLowerThanExchangeRsl(exchange, company))
                .shouldSellByStopPrice(isCurrentPriceLowerThanStopPrice(company, currentNotionalSalesPrice))
                .build();
    }

    private boolean isCurrentPriceLowerThanStopPrice(QueryResult.CompanyResult quotes, BigDecimal currentStopPrice) {
        return quotes.getWeeklyPrice().min(currentStopPrice).equals(quotes.getWeeklyPrice());
    }

    private boolean isCompanyRslLowerThanExchangeRsl(Exchange exchange, QueryResult.CompanyResult company) {
        return company.getRsl() < exchange.getRsl();
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

    private ExchangeResult createExchangeResult(Exchange exchange) {
        return new ExchangeResult(exchange.getName(), exchange.getRsl());
    }

    private PurchaseRecommendation createBuyRecommendation(Exchange exchange,
                                                           QueryResult.CompanyResult company,
                                                           IndividualRisk individualRisk) {

        final MoneyManagement.Parameters parameters = new MoneyManagement.Parameters(
                company.getWeeklyPrice(), company.getCurrentStopPrice(), exchange.getTransactionCosts());
        final int quantity = calculateQuantity(individualRisk.calculateIndividualPositionRisk(), parameters);

        return createPurchaseRecommendation(company, quantity);
    }

    private PurchaseRecommendation createPurchaseRecommendation(QueryResult.CompanyResult company, int quantity) {
        return PurchaseRecommendation.builder()
                .symbol(company.getSymbol())
                .name(company.getName())
                .rsl(company.getRsl())
                .price(company.getWeeklyPrice())
                .notionalSalesPrice(company.getCurrentStopPrice())
                .quantity(quantity)
                .build();
    }

}
