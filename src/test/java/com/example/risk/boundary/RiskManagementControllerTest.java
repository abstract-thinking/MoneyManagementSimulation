package com.example.risk.boundary;

import com.example.risk.boundary.api.RiskResult;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class RiskManagementControllerTest {

    @Autowired
    private RiskManagementController boundary;

    @Disabled("Missing test data")
    @Test
    public void shouldGetRiskManagementResult() {
        RiskResult riskResult = boundary.riskManagement(1L);

        assertThat(riskResult).isNotNull();
    }

    @Test
    public void shouldThrowIfUnknownRiskManagementId() {
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> boundary.riskManagement(666L))
                .withMessage("404 NOT_FOUND");
    }
}