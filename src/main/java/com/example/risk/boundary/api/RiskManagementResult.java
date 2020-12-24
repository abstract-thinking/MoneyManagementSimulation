package com.example.risk.boundary.api;

import com.example.risk.data.Investment;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class RiskManagementResult extends RepresentationModel<RiskManagementResult> {

    Long id;

    BigDecimal totalCapital;

    double individualPositionRiskInPercent;

    BigDecimal individualPositionRisk;

    List<Investment> investments;

    BigDecimal totalInvestment;

    BigDecimal totalRevenue;

    BigDecimal totalLossAbs;

    BigDecimal depotRisk;

    double depotRiskInPercent;

    double totalRiskInPercent;
}
