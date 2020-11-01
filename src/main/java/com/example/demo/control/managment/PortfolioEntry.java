package com.example.demo.control.managment;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class PortfolioEntry {

    @NonNull
    String name;
    int quantity;
    @NonNull
    BigDecimal purchasePrice;
    @NonNull
    BigDecimal purchaseCost;
    @NonNull
    BigDecimal notionalSalesPrice;

    public BigDecimal getSum() {
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getNotionalRevenue() {
        return notionalSalesPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getProfitOrLoss() {
        return getSum().subtract(getNotionalRevenue()).subtract(purchaseCost);
    }
}