package com.example.mm.boundary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MoneyManagementBoundaryTest {

    @Autowired
    private MoneyManagementBoundary boundary;

    @Test
    public void shouldGetMoneyManagement() {
        RiskManagementApi moneyManagement = boundary.moneyManagement();

        assertThat(moneyManagement).isNotNull();
    }

}