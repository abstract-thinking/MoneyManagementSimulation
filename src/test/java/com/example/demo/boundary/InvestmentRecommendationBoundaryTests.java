package com.example.demo.boundary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InvestmentRecommendationBoundaryTests {

    @Autowired
    private InvestRecommendationBoundary boundary;

    @Test
    public void shouldGetInvestments() {
        boundary.investments();
    }

    @Test
    public void shouldGetTopRsl() {
        boundary.rslCompanies();
    }
}
