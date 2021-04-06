package com.example.risk.data;

import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.control.management.caclulate.PriceCalculator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Slf4j
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

    private BigDecimal notionalSalesPrice;

    // TODO: This are not static. It looks that I'm mixing two responsibilities in one
    private BigDecimal currentPrice;
    private double rsl;
    private double exchangeRsl;

    // TODO: This seems to be wrong the investment should not know about the riskManagementId
    // Do I need relation table between?
    private Long riskManagementId;

    public BigDecimal getPriceRisk() {
        return purchasePrice.subtract(notionalSalesPrice);
    }

    public BigDecimal getInvestment() {
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal calculateNotionalRevenue() {
        return calculateNotionalSalesPrice().multiply(BigDecimal.valueOf(quantity)).subtract(transactionCosts);
    }

    private BigDecimal calculateNotionalSalesPrice() {
        return PriceCalculator.calculateNotionalSalesPrice(rsl, currentPrice, exchangeRsl);
    }

    public BigDecimal getPositionRisk() {
        final BigDecimal profitOrLoss = calculateNotionalRevenue().subtract(getInvestment());
        return isLoss(profitOrLoss) ? profitOrLoss.negate() : BigDecimal.ZERO;
    }

    private boolean isLoss(BigDecimal profitOrLoss) {
        return profitOrLoss.compareTo(BigDecimal.ZERO) < 0;
    }

    public InvestmentResult toApi() {
        return InvestmentResult.builder()
                .id(id)
                .wkn(wkn)
                .name(name)
                .quantity(quantity)
                .purchasePrice(purchasePrice)
                .notionalSalesPrice(notionalSalesPrice)
                .transactionCosts(transactionCosts)
                .investment(getInvestment())
                .notionalRevenue(calculateNotionalRevenue())
                .positionRisk(getPositionRisk())
                .build();
    }
}