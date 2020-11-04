package com.example.demo.control.management;

import com.example.demo.boundary.MoneyManagement;
import com.example.demo.data.MoneyManagementValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.demo.control.managment.QuantityCalculator.calculateQuantity;
import static org.assertj.core.api.Assertions.assertThat;

class QuantityCalculatorTest {

    private static final double EXCHANGE_RSL = 1.05;

    private static final BigDecimal PURCHASE_COST = BigDecimal.valueOf(30);

    private static final double POSITION_RISK = 2;

    private MoneyManagement moneyManagement;

    @BeforeEach
    public void setUp() {
        MoneyManagementValues moneyManagementValues = MoneyManagementValues.builder()
                .totalCapital(BigDecimal.valueOf(31500))
                .individualPositionRiskInPercent(2)
                .build();

        moneyManagement = new MoneyManagement(moneyManagementValues);
    }

    @Test
    public void calculate() {
        Map<BigDecimal, Double> values = new LinkedHashMap<>();
        values.put(new BigDecimal("396.00"), 1.63);
        values.put(new BigDecimal("374.70"), 1.52);
        values.put(new BigDecimal("330.60"), 1.37);
        values.put(new BigDecimal("70.60"), 1.27);
        values.put(new BigDecimal("105.38"), 1.24);
        values.put(new BigDecimal("1046.00"), 1.24);
        values.put(new BigDecimal("364.90"), 1.23);

        List<Integer> expected = new ArrayList<>();
        expected.add(4);
        expected.add(5);
        expected.add(7);
        expected.add(49);
        expected.add(37);
        expected.add(3);
        expected.add(11);
        int i = 0;
        for (Map.Entry<BigDecimal, Double> entry : values.entrySet()) {
            int quantity = calculateQuantity(
                    entry.getValue(),
                    entry.getKey(),
                    EXCHANGE_RSL,
                    moneyManagement.getIndividualPositionRisk(),
                    PURCHASE_COST);

            System.out.println(quantity);
            assertThat(quantity).isEqualTo(expected.get(i++));
        }
    }
}