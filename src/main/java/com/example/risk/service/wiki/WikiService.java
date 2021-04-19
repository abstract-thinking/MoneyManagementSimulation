package com.example.risk.service.wiki;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class WikiService {

    private static final String URL_FORMAT = "https://en.wikipedia.org/wiki/%s#Components";

    @Cacheable("fetchCompanies")
    public List<Company> fetchCompanies(String exchange) {
        ResponseEntity<String> result = new RestTemplate().getForEntity(String.format(URL_FORMAT, exchange), String.class);

        return parseContent(result.getBody());
    }

    private List<Company> parseContent(String content) {
        Document doc = Jsoup.parse(content);

        Element table = doc.select("#constituents").get(0);
        Elements rows = table.select("tr");

        List<Company> companies = new ArrayList<>();
        for (int i = 1; i < rows.size(); ++i) {
            Elements cols = rows.get(i).select("td");
            companies.add(new Company(cols.get(0).text(), cols.get(1).text()));
        }

        return companies;
    }

}
