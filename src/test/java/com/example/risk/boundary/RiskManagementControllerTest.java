package com.example.risk.boundary;

import com.example.risk.boundary.api.RiskResult;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import com.example.risk.data.InvestmentRepository;
import com.example.risk.data.RiskManagementRepository;
import com.example.risk.service.tradier.TradierService;
import com.example.risk.service.wiki.Company;
import com.example.risk.service.wiki.WikiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.example.risk.provider.TestDataProvider.createInvestment;
import static com.example.risk.provider.TestDataProvider.createQuotes;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest
class RiskManagementControllerTest {

    @MockBean
    private WikiService mockWikiService;

    @MockBean
    private TradierService mockTradierService;

    @Autowired
    private RiskManagementController controller;


    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private RiskManagementRepository riskManagementRepository;

    IndividualRisk individualRisk;
    Investment investment;

    @BeforeEach
    void database() {
        riskManagementRepository.deleteAll();
        individualRisk = riskManagementRepository.save(new IndividualRisk(BigDecimal.valueOf(10000), 1, "test"));

        investmentRepository.deleteAll();
        investment = investmentRepository.save(createInvestment(individualRisk.getId()));
    }


    @Test
    public void shouldGetRiskManagementResult() {
        Company company = new Company("Apple Inc.", "AAPL");
        when(mockWikiService.fetchCompanies("NASDAQ-100")).thenReturn(List.of(company));
        when(mockTradierService.fetchWeeklyQuotes("AAPL")).thenReturn(CompletableFuture.completedFuture(createQuotes()));

        RiskResult riskResult = controller.riskManagement(individualRisk.getId());


        riskResult.getId();
    }

    @Test
    public void shouldThrowIfUnknownRiskManagementId() {
        Company company = new Company("Unknown", "XYZ");
        when(mockWikiService.fetchCompanies("NASDAQ-100")).thenReturn(List.of(company));
        when(mockTradierService.fetchWeeklyQuotes("XYZ")).thenThrow(new ResponseStatusException(NOT_FOUND));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> controller.riskManagement(individualRisk.getId()))
                .withMessage("404 NOT_FOUND");
    }
}