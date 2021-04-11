package com.example.risk.control.invest;

import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.boundary.api.SalesRecommendationMetadata;
import com.example.risk.control.management.calculate.InvestmentRecommender;
import com.example.risk.service.rsl.RslService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Disabled
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

        // FIXME:
        SalesRecommendationMetadata saleRecommendations = recommender.findSaleRecommendations(null);

        assertThat(saleRecommendations.getSaleRecommendations()).hasSize(2);
        assertThat(saleRecommendations.getSaleRecommendations()).contains(
                createRecommendation("PayPal Holdings Inc.", 1.08),
                createRecommendation("Fiserv Inc.", 0.94));
    }

    private SaleRecommendation createRecommendation(String company, Double companyRsl) {
        return SaleRecommendation.builder()
                .name(company)
                .rsl(companyRsl)
                .build();
    }
}