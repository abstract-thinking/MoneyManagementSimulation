package com.example.demo.control.invest;

import com.example.demo.boundary.CompanyResult;
import com.example.demo.boundary.InvestmentRecommendation;
import com.example.demo.data.Investment;
import com.example.demo.data.InvestmentRepository;
import com.example.demo.service.rsl.RslService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparingDouble;

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
        Map<String, Double> result = parseContent(content);

        List<InvestmentRecommendation> recommendations = new ArrayList<>();
        for (Investment entry : repository.findAll()) {
            recommendations.add(createRecommendation(entry.getName(), result));
        }

        return recommendations;
    }

    private Map<String, Double> parseContent(String content) {
        Document doc = Jsoup.parse(content);

        Element table = doc.select("table").get(0);
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
        Map<String, Double> result = parseContent(content);

        List<CompanyResult> companyResults = new ArrayList<>();
        for (Map.Entry<String, Double> entry : result.entrySet()) {
            companyResults.add(new CompanyResult(entry.getKey(), entry.getValue()));
        }

        companyResults.sort(comparingDouble(CompanyResult::getRsl).reversed());

        return companyResults.subList(0, 10);
    }
}


