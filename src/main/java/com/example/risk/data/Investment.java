package com.example.risk.data;

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

    // Useless
    private BigDecimal notionalSalesPrice;

    // TODO: This seems to be wrong the investment should not know about the riskManagementId
    // Do I need relation table between?
    private Long riskManagementId;

    public BigDecimal getPriceRisk() {
        return purchasePrice.subtract(notionalSalesPrice);
    }

    public BigDecimal getInvestment() {
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }

}