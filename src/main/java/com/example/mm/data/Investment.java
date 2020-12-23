package com.example.mm.data;

import com.example.mm.boundary.SellRecommendation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Investment {

    @Id
    @GeneratedValue
    private Long id;

    String wkn;
    String name;
    int quantity;
    BigDecimal purchasePrice;
    BigDecimal transactionCosts;
    BigDecimal notionalSalesPrice;
    BigDecimal weeklyNotionalSalesPrice;

    private Long moneyManagementId;

    public BigDecimal getInvestment() {
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getNotionalRevenue() {
        return notionalSalesPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getProfitOrLoss() {
        return getNotionalRevenue().subtract(getInvestment()).subtract(transactionCosts);
    }

    public BigDecimal getRisk() {
        BigDecimal risk = getProfitOrLoss().negate();

        return risk.compareTo(BigDecimal.ZERO) > 0 ? risk : BigDecimal.ZERO;
    }

    public boolean shouldSell(BigDecimal individualPositionRisk, SellRecommendation sellRecommendation) {
        return shouldSellBy(individualPositionRisk) || shouldSellBy(sellRecommendation);
    }

    private boolean shouldSellBy(BigDecimal individualPositionRisk) {
        return isPositive() && getRisk().compareTo(individualPositionRisk) >= 0;
    }

    private boolean shouldSellBy(SellRecommendation sellRecommendation) {
        return isNotPositive() && sellRecommendation.shouldSell();
    }

    private boolean isPositive() {
        return getRisk().signum() == 1;
    }

    private boolean isNotPositive() {
        return !isPositive();
    }

    public BigDecimal getPositionRisk() {
        return purchasePrice.subtract(notionalSalesPrice);
    }
}