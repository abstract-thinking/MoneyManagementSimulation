package com.example.demo.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;

@Data
@EqualsAndHashCode
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class MoneyManagement {

    @Id
    @GeneratedValue
    private Long id;

    BigDecimal totalCapital;

    double individualPositionRiskInPercent;

    @OneToMany(mappedBy = "moneyManagement",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    List<Investment> investments;

    public BigDecimal getIndividualPositionRisk() {
        return totalCapital.multiply(BigDecimal.valueOf(individualPositionRiskInPercent / 100));
    }

    public double getPortfolioRiskInPercent() {
        return getTotalLossAbs().doubleValue() / getTotalSum().doubleValue() * 100;
    }

    public double getTotalRiskInPercent() {
        return getTotalLossAbs().doubleValue() / totalCapital.doubleValue() * 100;
    }

    public BigDecimal getTotalSum() {
        return investments.stream()
                .map(Investment::getSum)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalRevenue() {
        return investments.stream()
                .map(Investment::getNotionalRevenue)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalLossAbs() {
        return investments.stream()
                .filter(i -> i.getProfitOrLoss().signum() == -1)
                .map(Investment::getProfitOrLoss)
                .reduce(ZERO, BigDecimal::add)
                .abs();
    }


}
