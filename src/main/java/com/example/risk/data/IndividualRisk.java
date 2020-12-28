package com.example.risk.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

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

    public IndividualRisk(BigDecimal totalCapital, double individualPositionRiskInPercent) {
        this.totalCapital = totalCapital;
        this.individualPositionRiskInPercent = individualPositionRiskInPercent;
    }
}