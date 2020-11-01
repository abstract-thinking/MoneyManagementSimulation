package com.example.demo.boundary;


import com.example.demo.control.invest.InvestmentRecommender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@RequestMapping("/recommendations")
@Controller
@AllArgsConstructor
public class InvestRecommendationBoundary {

    private final InvestmentRecommender recommender;

    @GetMapping("/investment")
    public List<InvestmentRecommendation> investments() {
        log.info("Investment recommendation invoked");

        return recommender.getRecommendations();
    }

    @GetMapping("/companies")
    public List<CompanyResult> rslCompanies() {
        return recommender.getTopRsl();
    }
}

