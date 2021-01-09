package com.example.risk.boundary.api;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SellRecommendationTest {

    @Test
    void shouldSell() {
        SellRecommendation sellRecommendation = SellRecommendation.builder()
                .initialNotionalSalesPrice(BigDecimal.valueOf(100))
                .price(BigDecimal.valueOf(100))
                .build();

        boolean shouldSell = sellRecommendation.shouldSell();

        assertThat(shouldSell).isTrue();
    }

    @Test
    void shouldNotSell() {
        SellRecommendation sellRecommendation = SellRecommendation.builder()
                .initialNotionalSalesPrice(BigDecimal.valueOf(100))
                .price(BigDecimal.valueOf(99))
                .exchangeRsl(1.1)
                .companyRsl(1.2)
                .build();

        boolean shouldSell = sellRecommendation.shouldSell();

        assertThat(shouldSell).isFalse();
    }

    @Test
    void shouldSellBySellRecommendation() {
        SellRecommendation sellRecommendation = createSellRecommendation(1.02, 1.03);

        boolean shouldSell = sellRecommendation.shouldSell();

        assertThat(shouldSell).isTrue();
    }

    @Test
    void shouldNotSellBySellRecommendation() {
        SellRecommendation sellRecommendation = createSellRecommendation(1.1, 1.1);

        boolean shouldSell = sellRecommendation.shouldSell();

        assertThat(shouldSell).isFalse();
    }

    private SellRecommendation createSellRecommendation(double rsl, double exchangeRsl) {
        return SellRecommendation.builder()
                .initialNotionalSalesPrice(BigDecimal.valueOf(100))
                .price(BigDecimal.ZERO)
                .companyRsl(rsl)
                .exchangeRsl(exchangeRsl)
                .build();
    }
}