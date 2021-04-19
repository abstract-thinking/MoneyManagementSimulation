package com.example.risk.service.tradier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
public class TradierService {

    private static final String URL = "https://sandbox.tradier.com/v1/markets/history";

    private static final int RSL_WEEKS = 27;

    @Value("${tradier.key}")
    private String key;

    @Cacheable("fetchWeeklyQuotes")
    public CompletableFuture<List<Quote>> fetchWeeklyQuotes(String symbol) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "Bearer " + key);
        headers.set(ACCEPT, APPLICATION_JSON_VALUE);

        LocalDate now = LocalDate.now();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL)
                .queryParam("symbol", symbol)
                .queryParam("interval", "weekly")
                .queryParam("start", now.minusWeeks(RSL_WEEKS * 3).toString())
                .queryParam("end", now.toString());

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<HistoryQuotes> response = new RestTemplate()
                .exchange(builder.toUriString(), HttpMethod.GET, entity, HistoryQuotes.class);

        if (response.hasBody() && response.getBody() != null) {
            return CompletableFuture.completedFuture(response.getBody().getHistory().getWeekly());
        } else {
            log.error("Missing data for " + symbol);
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
    }

}
