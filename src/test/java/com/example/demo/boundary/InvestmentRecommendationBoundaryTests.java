package com.example.demo.boundary;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InvestmentRecommendationBoundaryTests {

    @Autowired
    private InvestRecommendationBoundary boundary;

    @Test
    public void shouldGetInvestments() {
        boundary.investments();
    }

    @Disabled
    @Test
    public void shouldGetTopRsl() {
        boundary.rslCompanies();
    }
}
