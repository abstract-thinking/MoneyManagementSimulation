package com.example.demo.service.rsl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class RslServiceIT {

    @Autowired
    private RslService rslService;

    @Test
    public void shouldFetchExchangeRates() {
        String content = rslService.fetchTable();

        log.error(content);
        assertThat(content).isNotEmpty();
    }
}

