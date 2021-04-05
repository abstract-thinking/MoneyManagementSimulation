package com.example.risk.data;

import com.example.risk.boundary.api.InvestmentResult;
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
    private BigDecimal currentPrice;

    // TODO: This seems to be wrong the investment should not know about the riskManagementId
    // Do I need relation table between?
    private Long riskManagementId;

    // TODO: Maybe into another table as well.
    private BigDecimal risk;

    public BigDecimal getPriceRisk() {
        return purchasePrice.subtract(notionalSalesPrice);
    }

    public BigDecimal getInvestmentRisk() {
        if (risk == null) {
            throw new RuntimeException("Illegal use: Risk must calculated! " + name);
        }

        return risk;
    }

    public BigDecimal getInvestment() {
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getNotionalRevenue() {
        return determineSalesPrice().multiply(BigDecimal.valueOf(quantity));
    }

    private BigDecimal determineSalesPrice() {
        log.info("Determine notional sales price for {}. Initial {} or current {}.", name, notionalSalesPrice,
                currentPrice);
        if (notionalSalesPrice.compareTo(currentPrice) < 0) {
            log.info("Determine initial as sales price {}", notionalSalesPrice);
            return currentPrice;
        } else {
            log.info("Determine current as sales price {}", currentPrice);
            return currentPrice;
        }
    }

    public BigDecimal getProfitOrLoss() {
        return getNotionalRevenue().subtract(getInvestment()).subtract(transactionCosts);
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
                .notionalRevenue(getNotionalRevenue())
                .positionRisk(getInvestmentRisk())
                .build();
    }
}