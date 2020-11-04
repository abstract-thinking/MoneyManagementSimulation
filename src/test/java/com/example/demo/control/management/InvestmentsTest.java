package com.example.demo.control.management;

import com.example.demo.control.managment.Investments;
import com.example.demo.data.Investment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;

class InvestmentsTest {

    private Investments investments;

    @BeforeEach
    private void setUp() {
        investments = createInvestments();
    }

    public static Investments createInvestments() {
        List<Investment> investments = new ArrayList<>();
        investments.add(createPortfolioEntry("ABC", TEN, 10, BigDecimal.valueOf(20)));
        investments.add(createPortfolioEntry("DEF", TEN, 1, BigDecimal.valueOf(5)));

        return new Investments(investments);
    }

    private static Investment createPortfolioEntry(String name, BigDecimal purchasePrice, int quality, BigDecimal notationalSalesPrice) {
        return Investment.builder()
                .name(name)
                .purchasePrice(purchasePrice)
                .quantity(quality)
                .notionalSalesPrice(notationalSalesPrice)
                .purchaseCost(ONE)
                .build();
    }

    @Test
    void shouldGetTotalSum() {
        assertThat(investments.getTotalSum()).isEqualTo(BigDecimal.valueOf(110));
    }

    @Test
    void shouldGetTotalRevenue() {
        assertThat(investments.getTotalRevenue()).isEqualTo(BigDecimal.valueOf(205));
    }

    @Test
    void shouldGetTotalLossAbs() {
        assertThat(investments.getTotalLossAbs()).isEqualTo(BigDecimal.valueOf(6));
    }

}