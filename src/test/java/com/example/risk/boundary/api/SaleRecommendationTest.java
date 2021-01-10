package com.example.risk.boundary.api;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SaleRecommendationTest {

    @Test
    void shouldSell() {
        SaleRecommendation saleRecommendation = SaleRecommendation.builder()
                .initialNotionalSalesPrice(BigDecimal.valueOf(100))
                .price(BigDecimal.valueOf(100))
                .build();

        boolean shouldSell = saleRecommendation.shouldSell();

        assertThat(shouldSell).isTrue();
    }

    @Test
    void shouldNotSell() {
        SaleRecommendation saleRecommendation = SaleRecommendation.builder()
                .initialNotionalSalesPrice(BigDecimal.valueOf(100))
                .price(BigDecimal.valueOf(99))
                .exchangeRsl(1.1)
                .companyRsl(1.2)
                .build();

        boolean shouldSell = saleRecommendation.shouldSell();

        assertThat(shouldSell).isFalse();
    }

    @Test
    void shouldSellBySellRecommendation() {
        SaleRecommendation saleRecommendation = createSellRecommendation(1.02, 1.03);

        boolean shouldSell = saleRecommendation.shouldSell();

        assertThat(shouldSell).isTrue();
    }

    @Test
    void shouldNotSellBySellRecommendation() {
        SaleRecommendation saleRecommendation = createSellRecommendation(1.1, 1.1);

        boolean shouldSell = saleRecommendation.shouldSell();

        assertThat(shouldSell).isFalse();
    }

    private SaleRecommendation createSellRecommendation(double rsl, double exchangeRsl) {
        return SaleRecommendation.builder()
                .initialNotionalSalesPrice(BigDecimal.valueOf(100))
                .price(BigDecimal.ZERO)
                .companyRsl(rsl)
                .exchangeRsl(exchangeRsl)
                .build();
    }
}