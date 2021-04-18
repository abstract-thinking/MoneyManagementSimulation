package com.example.risk.service.finanztreff;

import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class DecisionRowConverter {

    private final RslService rslService;

    public ExchangeSnapshot fetchTable() {
        String content = rslService.fetchTable();

        return new ExchangeSnapshot(parseContent(content));
    }

    private List<ExchangeSnapshot.Quotes> parseContent(String content) {
        Document doc = Jsoup.parse(content);

        //  <table class="extendetKursliste filterTable ov">
        Element table = doc.select("table").get(3);
        Elements rows = table.select("tr");

        List<ExchangeSnapshot.Quotes> result = new ArrayList<>();
        for (int i = 2; i < rows.size(); ++i) {
            Elements cols = rows.get(i).select("td");
            result.add(ExchangeSnapshot.Quotes.builder()
                    .wkn(cols.get(1).text())
                    .name(cols.get(2).text())
                    .price(BigDecimal.valueOf(parseDouble(cols.get(4).text())))
                    .rsl(parseDouble(cols.get(6).text()))
                    .build());
        }

        return result;
    }

    private Double parseDouble(String s) {
        try {
            return Double.parseDouble(s.replace(".", "").replace(",", "."));
        } catch (NumberFormatException nfe) {
            return Double.NaN;
        }
    }

}