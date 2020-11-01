package com.example.demo.control.managment;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;

@AllArgsConstructor
public class Portfolio {

    private final List<PortfolioEntry> portfolioEntries;

    public BigDecimal getTotalSum() {
        return portfolioEntries.stream()
                .map(PortfolioEntry::getSum)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalRevenue() {
        return portfolioEntries.stream()
                .map(PortfolioEntry::getNotionalRevenue)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalLossAbs() {
        return portfolioEntries.stream()
                .filter(i -> i.getProfitOrLoss().signum() == -1)
                .map(PortfolioEntry::getProfitOrLoss)
                .reduce(ZERO, BigDecimal::add)
                .abs();
    }

}
