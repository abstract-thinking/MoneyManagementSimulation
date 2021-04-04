package com.example.risk.boundary.api;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class RiskData {

    BigDecimal totalCapital;
    Double individualPositionRiskInPercent;

    private RiskData() {
        totalCapital = BigDecimal.ZERO;
        individualPositionRiskInPercent = Double.NaN;
    }
}
