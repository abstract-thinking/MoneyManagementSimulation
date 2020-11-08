package com.example.demo.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Investment {

    @Id
    @GeneratedValue
    private Long id;

    String name;
    int quantity;
    BigDecimal purchasePrice;
    BigDecimal purchaseCost;
    BigDecimal notionalSalesPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "investment_id", nullable = false)
    private MoneyManagement moneyManagement;

    public BigDecimal getSum() {
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getNotionalRevenue() {
        return notionalSalesPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getProfitOrLoss() {
        return getNotionalRevenue().subtract(getSum()).subtract(purchaseCost);
    }
}