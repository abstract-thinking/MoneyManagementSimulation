package com.example.risk.boundary;

import com.example.risk.boundary.api.RiskManagementResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RiskManagementBoundaryTest {

    @Autowired
    private RiskManagementBoundary boundary;

    @Test
    public void shouldGetMoneyManagement() {
        RiskManagementResult moneyManagement = boundary.riskManagement();

        assertThat(moneyManagement).isNotNull();
    }

}