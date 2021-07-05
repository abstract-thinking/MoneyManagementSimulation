package com.example.risk.control.invest;

import com.example.risk.boundary.api.Exchange;
import com.example.risk.boundary.api.QueryResult;
import com.example.risk.boundary.api.SaleRecommendations;
import com.example.risk.data.Investment;
import com.example.risk.service.InvestmentRecommender;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.example.risk.provider.TestDataProvider.createInvestment;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
class InvestRecommenderTests {

    private static final double EXCHANGE_RSL = 1.151;

    private static final BigDecimal WEEKLY_PRICE = BigDecimal.valueOf(130.4500);
    private static final BigDecimal WEEKLY_STOP_PRICE = BigDecimal.valueOf(130.4400);

    private final InvestmentRecommender recommender = new InvestmentRecommender();

    private final QueryResult queryResult = new QueryResult(createExchange());

    private Exchange createExchange() {
        Exchange exchange = new Exchange("MyExchange", "ME", BigDecimal.valueOf(60));
        exchange.setRsl(EXCHANGE_RSL);

        return exchange;
    }

    @Test
    public void shouldGetNoRecommendations() {
        queryResult.add(createCompanyResult(EXCHANGE_RSL, WEEKLY_STOP_PRICE));

        SaleRecommendations saleRecommendations = recommender.findSaleRecommendations(queryResult, createInvestments());

        assertThat(saleRecommendations.getSaleRecommendations()).isEmpty();
    }

    @Test
    public void shouldGetRecommendationByRslComparison() {
        queryResult.add(createCompanyResult(EXCHANGE_RSL - 0.001, WEEKLY_STOP_PRICE));

        SaleRecommendations saleRecommendations = recommender.findSaleRecommendations(queryResult, createInvestments());

        assertThat(saleRecommendations.getSaleRecommendations()).hasSize(1);
        assertThat(saleRecommendations.getSaleRecommendations().get(0).isShouldSellByRslComparison()).isTrue();
        assertThat(saleRecommendations.getSaleRecommendations().get(0).isShouldSellByStopPrice()).isFalse();
    }

    @Test
    public void shouldGetRecommendationByCurrentStopPrice() {
        queryResult.add(createCompanyResult(EXCHANGE_RSL, WEEKLY_PRICE));

        SaleRecommendations saleRecommendations = recommender.findSaleRecommendations(queryResult, createInvestments());

        assertThat(saleRecommendations.getSaleRecommendations()).hasSize(1);
        assertThat(saleRecommendations.getSaleRecommendations().get(0).isShouldSellByStopPrice()).isTrue();
        assertThat(saleRecommendations.getSaleRecommendations().get(0).isShouldSellByRslComparison()).isFalse();
    }

    private QueryResult.CompanyResult createCompanyResult(double rsl, BigDecimal currentStopPrice) {
        return QueryResult.CompanyResult.builder()
                .symbol("AAPL")
                .name("Apple Inc.")
                .currentStopPrice(currentStopPrice)
                .weeklyPrice(WEEKLY_PRICE)
                .rsl(rsl)
                .build();
    }

    private List<Investment> createInvestments() {
        return List.of(createInvestment(1L));
    }

}