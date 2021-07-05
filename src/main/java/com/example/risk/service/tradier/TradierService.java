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
        final String uri = createUri(symbol, LocalDate.now());
        HttpEntity<HistoryQuotes> response = new RestTemplate()
                .exchange(uri, HttpMethod.GET, new HttpEntity<>(createHeaders()), HistoryQuotes.class);

        log.info("X-Ratelimit-Allowed: {}", response.getHeaders().get("X-Ratelimit-Allowed"));
        log.info("X-Ratelimit-Used: {}", response.getHeaders().get("X-Ratelimit-Used"));
        log.info("X-Ratelimit-Available: {}", response.getHeaders().get("X-Ratelimit-Available"));
        log.info("X-Ratelimit-Expiry: {}", response.getHeaders().get("X-Ratelimit-Expiry"));

        if (response.getBody() != null && response.getBody().getHistory() != null) {
            log.info("Processing data for " + symbol);
            return CompletableFuture.completedFuture(response.getBody().getHistory().getWeekly());
        } else {
            log.error("Missing data for " + symbol);
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "Bearer " + key);
        headers.set(ACCEPT, APPLICATION_JSON_VALUE);
        return headers;
    }

    private String createUri(String symbol, LocalDate now) {
        return UriComponentsBuilder.fromHttpUrl(URL)
                .queryParam("symbol", symbol)
                .queryParam("interval", "weekly")
                .queryParam("start", now.minusWeeks(RSL_WEEKS * 3).toString())
                .queryParam("end", now.toString())
                .toUriString();
    }

}
