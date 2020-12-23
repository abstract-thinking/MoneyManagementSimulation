package com.example.mm.converter;

import com.example.mm.control.invest.ExchangeResult;
import com.example.mm.service.rsl.RslService;
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

    public List<ExchangeResult> fetchTable() {
        String content = rslService.fetchTable();

        return parseContent(content);
    }

    private List<ExchangeResult> parseContent(String content) {
        Document doc = Jsoup.parse(content);

        //  <table class="extendetKursliste filterTable ov">
        Element table = doc.select("table").get(3);
        Elements rows = table.select("tr");

        List<ExchangeResult> result = new ArrayList<>();
        for (int i = 2; i < rows.size(); ++i) {
            Elements cols = rows.get(i).select("td");
            result.add(ExchangeResult.builder()
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
        try {
            return Double.parseDouble(s.replace(".", "").replace(",", "."));
        } catch (NumberFormatException nfe) {
            return Double.NaN;
        }
    }

}