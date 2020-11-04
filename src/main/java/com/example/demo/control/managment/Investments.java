package com.example.demo.control.managment;

import com.example.demo.data.Investment;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;

@AllArgsConstructor
public class Investments {

    private final List<Investment> investments;

    public BigDecimal getTotalSum() {
        return investments.stream()
                .map(Investment::getSum)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalRevenue() {
        return investments.stream()
                .map(Investment::getNotionalRevenue)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalLossAbs() {
        return investments.stream()
                .filter(i -> i.getProfitOrLoss().signum() == -1)
                .map(Investment::getProfitOrLoss)
                .reduce(ZERO, BigDecimal::add)
                .abs();
    }

}
