package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class InvestmentResult extends RepresentationModel<RiskResult> {

    private Long id;

    private String wkn;
    private String name;
    private int quantity;
    private BigDecimal purchasePrice;
    private BigDecimal transactionCosts;
    private BigDecimal investment;
    private BigDecimal notionalSalesPrice;
    private BigDecimal notionalRevenue;
    private BigDecimal positionRisk;

    private SellRecommendation sellRecommendation;

    public boolean getHasSellRecommendation() {
        return sellRecommendation != null;
    }

}
