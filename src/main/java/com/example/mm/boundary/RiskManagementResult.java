package com.example.mm.boundary;

import com.example.mm.data.Investment;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
@ToString
public class RiskManagementResult {

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
