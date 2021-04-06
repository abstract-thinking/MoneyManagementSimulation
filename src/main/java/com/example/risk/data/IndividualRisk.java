package com.example.risk.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
public class IndividualRisk {

    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal totalCapital;

    private double individualPositionRiskInPercent;

    private String name;

    public IndividualRisk(BigDecimal totalCapital, double individualPositionRiskInPercent, String name) {
        this.totalCapital = totalCapital;
        this.individualPositionRiskInPercent = individualPositionRiskInPercent;
        this.name = name;
    }

    public BigDecimal calculateIndividualPositionRisk() {
        return totalCapital
                .multiply(BigDecimal.valueOf(individualPositionRiskInPercent))
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.DOWN);
    }
}
