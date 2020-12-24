package com.example.risk.boundary;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RecommendationControllerTests {

    @Autowired
    private RecommendationController controller;

    @Disabled
    @Test
    public void shouldGetBuyRecommendations() {
        controller.buyRecommendations();
    }

//    @Disabled
//    @Test
//    public void shouldGetSellRecommendations() {
//        controller.sellRecommendations();
//    }
}
