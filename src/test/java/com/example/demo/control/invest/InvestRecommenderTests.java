package com.example.demo.control.invest;

import com.example.demo.boundary.InvestmentRecommendation;
import com.example.demo.service.rsl.RslService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
public class InvestRecommenderTests {

    @MockBean
    private RslService rslService;

    @Autowired
    private InvestmentRecommender recommender;

    private String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    public void shouldGetRecommendations() {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:data/content.html");
        when(rslService.fetchTable()).thenReturn(asString(resource));

        List<InvestmentRecommendation> investmentRecommendations = recommender.getRecommendations();

        assertThat(investmentRecommendations).hasSize(2);
        assertThat(investmentRecommendations).contains(
                createRecommendation("PayPal Holdings Inc.", 1.08),
                createRecommendation("Fiserv Inc.", 0.94));
    }

    private InvestmentRecommendation createRecommendation(String company, Double companyRsl) {
        return InvestmentRecommendation.builder()
                .company(company)
                .companyRsl(companyRsl)
                .exchange("NASDAQ 100")
                .exchangeRsl(1.05)
                .build();
    }
}