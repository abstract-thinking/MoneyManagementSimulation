package com.example.demo.control.invest;

import com.example.demo.boundary.CompanyResult;
import com.example.demo.boundary.InvestmentRecommendation;
import com.example.demo.data.Investment;
import com.example.demo.data.InvestmentRepository;
import com.example.demo.service.rsl.RslService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparingDouble;

@Slf4j
@Component
public class InvestmentRecommender {

    private final String exchange = "NASDAQ 100";

    private final InvestmentRepository repository;

    private final RslService rslService;

    @Autowired
    public InvestmentRecommender(InvestmentRepository repository, RslService rslService) {
        this.repository = repository;
        this.rslService = rslService;
    }

    public List<InvestmentRecommendation> getRecommendations() {
        String content = rslService.fetchTable();
        log.info("Received content: {}", content);
        Map<String, Double> result = parseContent(content);

        List<InvestmentRecommendation> recommendations = new ArrayList<>();
        for (Investment entry : repository.findAll()) {
            recommendations.add(createRecommendation(entry.getName(), result));
        }

        return recommendations;
    }

    private Map<String, Double> parseContent(String content) {
        Document doc = Jsoup.parse(content);

        //  <table class="extendetKursliste filterTable ov">
        Element table = doc.select("table").get(3);
        Elements rows = table.select("tr");

        Map<String, Double> result = new HashMap<>();
        for (int i = 2; i < rows.size(); ++i) {
            Elements cols = rows.get(i).select("td");
            result.put(cols.get(2).text(), Double.parseDouble(cols.get(6).text().replace(',', '.')));
        }

        return result;
    }

    private InvestmentRecommendation createRecommendation(String company, Map<String, Double> result) {
        return InvestmentRecommendation.builder()
                .company(company)
                .companyRsl(result.get(company))
                .exchange(exchange)
                .exchangeRsl(result.get(exchange))
                .build();
    }

    public List<CompanyResult> getTopRsl() {
        String content = rslService.fetchTable();
        log.info("Received content: {}", content);

        Map<String, Double> result = parseContent(content);
        log.info("Result: {}", result);

        List<CompanyResult> companyResults = new ArrayList<>();
        for (Map.Entry<String, Double> entry : result.entrySet()) {
            companyResults.add(new CompanyResult(entry.getKey(), entry.getValue()));
        }

        companyResults.sort(comparingDouble(CompanyResult::getRsl).reversed());

        if (companyResults.size() < 10) {
            return Collections.emptyList();
        }

        return companyResults.subList(0, 10);
    }
}


