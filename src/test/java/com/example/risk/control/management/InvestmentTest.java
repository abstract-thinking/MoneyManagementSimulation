package com.example.risk.control.management;

import com.example.risk.boundary.api.SellRecommendation;
import com.example.risk.data.Investment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class InvestmentTest {

    private final Investment positiveInvestment = Investment.builder()
            .name("Positive Investment AG")
            .quantity(10)
            .purchasePrice(BigDecimal.TEN)
            .transactionCosts(BigDecimal.ONE)
            .notionalSalesPrice(BigDecimal.valueOf(20))
            .build();

    private final Investment negativeInvestment = Investment.builder()
            .name("Negative Investment AG")
            .quantity(10)
            .purchasePrice(BigDecimal.TEN)
            .transactionCosts(BigDecimal.ONE)
            .notionalSalesPrice(BigDecimal.ZERO)
            .build();

    @Test
    void shouldGetInvestment() {
        assertThat(positiveInvestment.getInvestment()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void shouldReturnInvestment() {
        Investment investment = Investment.builder()
                .quantity(41)
                .purchasePrice(BigDecimal.valueOf(50))
                .build();

        assertThat(investment.getInvestment()).isEqualTo(BigDecimal.valueOf(2050));
    }

    @Test
    void shouldGetNotationalRevenueResult() {
        assertThat(positiveInvestment.getNotionalRevenue()).isEqualTo(BigDecimal.valueOf(200));
    }

    @Test
    void shouldSell() {
        BigDecimal individualPositionRisk = BigDecimal.valueOf(101);

        assertThat(negativeInvestment.getRisk()).as("Should be positive").isPositive();
        assertThat(negativeInvestment.getRisk().compareTo(individualPositionRisk)).isEqualTo(0);

        boolean shouldSell = negativeInvestment.shouldSell(individualPositionRisk, emptySellRecommendation());

        assertThat(shouldSell).isTrue();
    }

    private SellRecommendation emptySellRecommendation() {
        return SellRecommendation.builder().build();
    }

    @Test
    void shouldNotSell() {
        BigDecimal individualPositionRisk = BigDecimal.valueOf(102);
        assertThat(negativeInvestment.getRisk()).as("Should be positive").isPositive();
        assertThat(negativeInvestment.getRisk().compareTo(individualPositionRisk)).isEqualTo(-1);

        boolean shouldSell = negativeInvestment.shouldSell(individualPositionRisk, emptySellRecommendation());

        assertThat(shouldSell).isFalse();
    }

    @Test
    void shouldSellBySellRecommendation() {
        SellRecommendation sellRecommendation = createSellRecommendation(1.02, 1.03);

        assertThat(positiveInvestment.getRisk()).as("Should be zero").isZero();
        assertThat(sellRecommendation.shouldSell()).isTrue();

        boolean shouldSell = positiveInvestment.shouldSell(BigDecimal.ZERO, sellRecommendation);

        assertThat(shouldSell).isTrue();
    }

    @Test
    void shouldNotSellBySellRecommendation2() {
        SellRecommendation notSellRecommendation = createSellRecommendation(1.1, 1.1);

        assertThat(positiveInvestment.getRisk()).as("Should be zero").isZero();
        assertThat(notSellRecommendation.shouldSell()).isFalse();

        boolean shouldSell = positiveInvestment.shouldSell(BigDecimal.ZERO, notSellRecommendation);

        assertThat(shouldSell).isFalse();
    }

    private SellRecommendation createSellRecommendation(double rsl, double exchangeRsl) {
        return SellRecommendation.builder()
                .companyRsl(rsl)
                .exchangeRsl(exchangeRsl)
                .build();
    }
}