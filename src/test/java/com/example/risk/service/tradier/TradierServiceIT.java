package com.example.risk.service.tradier;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class TradierServiceIT {

    @Autowired
    private TradierService tradierService;

    @Test
    public void shouldFetchData() throws ExecutionException, InterruptedException {
        CompletableFuture<List<Quote>> weeklyQuotesFuture = tradierService.fetchWeeklyQuotes("AAPL");

        List<Quote> weeklyQuotes = weeklyQuotesFuture.get();
        assertThat(weeklyQuotes).isNotEmpty();

        // log.info(weeklyQuotes.toString());
    }
}
