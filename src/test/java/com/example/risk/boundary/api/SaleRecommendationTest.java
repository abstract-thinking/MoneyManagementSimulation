package com.example.risk.boundary.api;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SaleRecommendationTest {

    @Test
    void shouldSell() {
        SaleRecommendation saleRecommendation = SaleRecommendation.builder()
                .shouldSellByRslComparison(true)
                .build();

        boolean shouldSell = saleRecommendation.shouldSell();

        assertThat(shouldSell).isTrue();
    }

    @Test
    void shouldNotSell() {
        SaleRecommendation saleRecommendation = SaleRecommendation.builder()
                .shouldSellByRslComparison(false)
                .build();

        boolean shouldSell = saleRecommendation.shouldSell();

        assertThat(shouldSell).isFalse();
    }

    @Test
    void shouldSellBySellRecommendation() {
        SaleRecommendation saleRecommendation = SaleRecommendation.builder()
                .shouldSellByFallingBelowTheLimit(true)
                .build();

        boolean shouldSell = saleRecommendation.shouldSell();

        assertThat(shouldSell).isTrue();
    }

    @Test
    void shouldNotSellBySellRecommendation() {
        SaleRecommendation saleRecommendation = SaleRecommendation.builder()
                .shouldSellByFallingBelowTheLimit(false)
                .build();

        boolean shouldSell = saleRecommendation.shouldSell();

        assertThat(shouldSell).isFalse();
    }
}