package com.example.demo.control.management;

import com.example.demo.control.managment.Portfolio;
import com.example.demo.control.managment.PortfolioEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.*;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;

class PortfolioTest {

    private Portfolio portfolio;

    @BeforeEach
    private void setUp() {
        portfolio = createPortfolio();
    }

    public static Portfolio createPortfolio() {
        List<PortfolioEntry> portfolioEntries = new ArrayList<>();
        portfolioEntries.add(createPortfolioEntry("ABC", TEN, 10, BigDecimal.valueOf(20)));
        portfolioEntries.add(createPortfolioEntry("DEF", TEN, 1, BigDecimal.valueOf(5)));

        return new Portfolio(portfolioEntries);
    }

    private static PortfolioEntry createPortfolioEntry(String name, BigDecimal purchasePrice, int quality, BigDecimal notationalSalesPrice) {
        return PortfolioEntry.builder()
                .name(name)
                .purchasePrice(purchasePrice)
                .quantity(quality)
                .notionalSalesPrice(notationalSalesPrice)
                .purchaseCost(ONE)
                .build();
    }

    @Test
    void shouldGetTotalSum() {
        assertThat(portfolio.getTotalSum()).isEqualTo(BigDecimal.valueOf(110));
    }

    @Test
    void shouldGetTotalRevenue() {
        assertThat(portfolio.getTotalRevenue()).isEqualTo(BigDecimal.valueOf(205));
    }

    @Test
    void shouldGetTotalLossAbs() {
        assertThat(portfolio.getTotalLossAbs()).isEqualTo(BigDecimal.valueOf(101));
    }

}