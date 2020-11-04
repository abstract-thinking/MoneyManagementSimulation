package com.example.demo.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@EqualsAndHashCode
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MoneyManagementValues {

    @Id
    @GeneratedValue
    private Long id;

    BigDecimal totalCapital;

    double individualPositionRiskInPercent;
}
