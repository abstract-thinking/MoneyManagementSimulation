package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class InvestmentResult extends RepresentationModel<InvestmentResult> {

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

    private SaleRecommendation saleRecommendation;

    public boolean getHasSellRecommendation() {
        return saleRecommendation != null;
    }

}
