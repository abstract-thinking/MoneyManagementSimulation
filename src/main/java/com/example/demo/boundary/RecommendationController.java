package com.example.demo.boundary;


import com.example.demo.control.invest.InvestmentRecommender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/recommendations")
@RestController
@AllArgsConstructor
public class RecommendationController {

    private final InvestmentRecommender recommender;

    @GetMapping(path = "/sell", produces = "application/json")
    public List<SellRecommendation> sellRecommendations() {
        return recommender.getSellRecommendations();
    }

    @GetMapping(path = "/buy", produces = "application/json")
    public List<BuyRecommendation> buyRecommendations() {
        return recommender.getBuyRecommendations();
    }
}

