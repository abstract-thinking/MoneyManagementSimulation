package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
@EqualsAndHashCode(callSuper = true)
public class RiskResult extends RepresentationModel<RiskResult> {

    Long id;

    String name;

    BigDecimal totalCapital;
    double individualPositionRiskInPercent;
    BigDecimal individualPositionRisk;

    List<InvestmentResult> investments;

    BigDecimal totalInvestment;
    BigDecimal totalRevenue;
    BigDecimal depotRisk;
    double depotRiskInPercent;

    double totalRiskInPercent;
}
