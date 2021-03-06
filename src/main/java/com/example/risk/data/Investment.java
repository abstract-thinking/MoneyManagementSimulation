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

    private String symbol;
    private String name;
    private int quantity;
    private BigDecimal purchasePrice;
    private BigDecimal transactionCosts;

    private BigDecimal stopPrice;
    private BigDecimal currentStopPrice;

    // TODO: This seems to be wrong the investment should not know about the riskManagementId
    // Do I need relation table between?
    private Long riskManagementId;

    public BigDecimal getInvestmentCapital() {
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }

}