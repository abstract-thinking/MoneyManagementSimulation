package com.example.risk.data;

import com.example.risk.boundary.api.InvestmentResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Investment {

    @Id
    @GeneratedValue
    private Long id;

    private String wkn;
    private String name;
    private int quantity;
    private BigDecimal purchasePrice;
    private BigDecimal transactionCosts;

    private BigDecimal initialNotionalSalesPrice;
    private BigDecimal currentNotionalSalesPrice;

    private Long moneyManagementId;

    public BigDecimal getInvestment() {
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getPositionRisk() {
        return purchasePrice.subtract(currentNotionalSalesPrice);
    }

    public BigDecimal getNotionalRevenue() {
        return determineNotionalSalesPrice().multiply(BigDecimal.valueOf(quantity));
    }

    private BigDecimal determineNotionalSalesPrice() {
        return initialNotionalSalesPrice.compareTo(currentNotionalSalesPrice) > 0 ?
                initialNotionalSalesPrice : currentNotionalSalesPrice;
    }

    public BigDecimal getRisk() {
        final BigDecimal profitOrLoss = getProfitOrLoss().negate();

        return profitOrLoss.compareTo(BigDecimal.ZERO) > 0 ? profitOrLoss : BigDecimal.ZERO;
    }

    private BigDecimal getProfitOrLoss() {
        return getNotionalRevenue().subtract(getInvestment()).subtract(transactionCosts);
    }

    public InvestmentResult toApi() {
        return InvestmentResult.builder()
                .id(id)
                .wkn(wkn)
                .name(name)
                .quantity(quantity)
                .purchasePrice(purchasePrice)
                .transactionCosts(transactionCosts)
                .notionalSalesPrice(determineNotionalSalesPrice())
                .investment(getInvestment())
                .notionalRevenue(getNotionalRevenue())
                .positionRisk(getRisk())
                .build();
    }
}