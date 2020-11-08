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
public class InvestRecommendationBoundary {

    private final InvestmentRecommender recommender;

    @GetMapping(path = "/investment", produces = "application/json")
    public List<InvestmentRecommendation> investments() {
        log.info("Investment recommendation invoked");

        return recommender.getRecommendations();
    }

    @GetMapping(path = "/companies", produces = "application/json")
    public List<CompanyResult> rslCompanies() {
        log.info("Companies invoked");

        return recommender.getTopRsl();
    }
}

