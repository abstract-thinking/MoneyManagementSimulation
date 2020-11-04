package com.example.demo.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@EqualsAndHashCode
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Investment {

    @Id
    @GeneratedValue
    private Long id;

    String name;
    int quantity;
    BigDecimal purchasePrice;
    BigDecimal purchaseCost;
    BigDecimal notionalSalesPrice;

    public BigDecimal getSum() {
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getNotionalRevenue() {
        return notionalSalesPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getProfitOrLoss() {
        return getNotionalRevenue().subtract(getSum()).subtract(purchaseCost);
    }
}