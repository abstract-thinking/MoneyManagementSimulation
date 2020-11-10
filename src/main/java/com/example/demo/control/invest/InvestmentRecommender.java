package com.example.demo.control.invest;

import com.example.demo.boundary.BuyRecommendation;
import com.example.demo.boundary.SellRecommendation;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.control.invest.PriceCalculator.calculateNotionalSalesPrice;
import static java.util.Comparator.comparingDouble;

@Slf4j
@Component
public class InvestmentRecommender {

    private static final String EXCHANGE_NAME = "NASDAQ 100";

    private final InvestmentRepository repository;

    private final RslService rslService;

    @Autowired
    public InvestmentRecommender(InvestmentRepository repository, RslService rslService) {
        this.repository = repository;
        this.rslService = rslService;
    }

    public List<SellRecommendation> getSellRecommendations() {
        String content = rslService.fetchTable();
        log.info("Received content: {}", content);
        List<DecisionRow> rows = parseContent(content);

        List<SellRecommendation> sellRecommendations = new ArrayList<>();
        for (Investment entry : repository.findAll()) {
            sellRecommendations.add(createSellRecommendation(entry.getName(), rows));
        }

        return sellRecommendations;
    }

    private List<DecisionRow> parseContent(String content) {
        Document doc = Jsoup.parse(content);

        //  <table class="extendetKursliste filterTable ov">
        Element table = doc.select("table").get(3);
        Elements rows = table.select("tr");

        List<DecisionRow> result = new ArrayList<>();
        for (int i = 2; i < rows.size(); ++i) {
            Elements cols = rows.get(i).select("td");
            result.add(DecisionRow.builder()
                    .wkn(cols.get(1).text())
                    .name(cols.get(2).text())
                    .price(BigDecimal.valueOf(parseDouble(cols.get(4).text())))
                    .vola30Day(parseDouble(cols.get(5).text()))
                    .rsl(parseDouble(cols.get(6).text()))
                    .build());
        }

        return result;
    }

    private Double parseDouble(String s) {
        return Double.parseDouble(
                s.replace(".", "")
                        .replace(",", "."));
    }

    private SellRecommendation createSellRecommendation(String companyName, List<DecisionRow> rows) {
        return SellRecommendation.builder()
                .company(companyName)
                .companyRsl(getRsl(rows, companyName))
                .exchange(EXCHANGE_NAME)
                .exchangeRsl(getRsl(rows, EXCHANGE_NAME))
                .build();
    }

    private double getRsl(List<DecisionRow> rows, String name) {
        return rows.stream()
                .filter(row -> row.getName().equals(name))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

    public List<BuyRecommendation> getBuyRecommendations() {
        String content = rslService.fetchTable();

        List<DecisionRow> rows = parseContent(content);
        rows.sort(comparingDouble(DecisionRow::getRsl).reversed());

        final double exchangeRsl = getRsl(rows, EXCHANGE_NAME);
        List<BuyRecommendation> buyRecommendations = new ArrayList<>(rows.size());
        for (DecisionRow row : rows) {
            buyRecommendations.add(new BuyRecommendation(row,
                    calculateNotionalSalesPrice(row.getRsl(), row.getPrice(), exchangeRsl)));
        }

        return buyRecommendations.stream()
                .filter(r -> r.getRsl() > exchangeRsl)
                .collect(Collectors.toList());
    }
}


