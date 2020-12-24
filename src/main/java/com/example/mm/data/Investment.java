package com.example.mm.data;

import com.example.mm.boundary.SellRecommendation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Investment {

    @Id
    @GeneratedValue
    private Long id;

    private String wkn;
    private String name;
    private int quantity;
    private BigDecimal purchasePrice;
    private BigDecimal transactionCosts;
    /**
     * Initial notional sales prices.
     */
    @Getter(AccessLevel.NONE)
    private BigDecimal notionalSalesPrice;
    @Getter(AccessLevel.NONE)
    private BigDecimal updatedNotionalSalesPrice;

    private Long moneyManagementId;

    public BigDecimal getHighestNotionalSalesPrice() {
        return isUpdatedNotionalSalesPriceHigher() ? updatedNotionalSalesPrice : notionalSalesPrice;
    }

    private boolean isUpdatedNotionalSalesPriceHigher() {
        return updatedNotionalSalesPrice != null && updatedNotionalSalesPrice.compareTo(notionalSalesPrice) > 0;
    }

    public BigDecimal getInvestment() {
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getPositionRisk() {
        return purchasePrice.subtract(getHighestNotionalSalesPrice());
    }

    public BigDecimal getNotionalRevenue() {
        return getHighestNotionalSalesPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getRisk() {
        final BigDecimal profitOrLoss = getProfitOrLoss().negate();

        return profitOrLoss.compareTo(BigDecimal.ZERO) > 0 ? profitOrLoss : BigDecimal.ZERO;
    }

    private BigDecimal getProfitOrLoss() {
        return getNotionalRevenue().subtract(getInvestment()).subtract(transactionCosts);
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
}