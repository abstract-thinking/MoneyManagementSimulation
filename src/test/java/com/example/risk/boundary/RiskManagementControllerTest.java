package com.example.risk.boundary;

import com.example.risk.boundary.api.RiskResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RiskManagementControllerTest {

    @Autowired
    private RiskManagementController boundary;

    @Test
    public void shouldGetRiskManagementResult() {
        RiskResult riskResult = boundary.riskManagement(1L);

        assertThat(riskResult).isNotNull();
    }

}