package com.example.mm.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@EqualsAndHashCode
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MoneyManagement {

    @Id
    @GeneratedValue
    private Long id;

    BigDecimal totalCapital;

    double individualPositionRiskInPercent;

    public BigDecimal getPositionRisk() {
        return totalCapital
                .multiply(BigDecimal.valueOf(individualPositionRiskInPercent))
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.DOWN);
    }

}
