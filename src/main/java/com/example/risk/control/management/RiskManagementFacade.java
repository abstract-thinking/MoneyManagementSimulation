package com.example.risk.control.management;

import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.RiskResults;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.control.management.caclulate.InvestmentRecommender;
import com.example.risk.control.management.caclulate.RiskManagementCalculator;
import com.example.risk.converter.DecisionRowConverter;
import com.example.risk.converter.ExchangeResult;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.IndividualRiskRepository;
import com.example.risk.data.Investment;
import com.example.risk.data.InvestmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.risk.control.management.caclulate.PriceCalculator.calculateNotionalSalesPrice;

@Slf4j
@Component
public class RiskManagementFacade {

    private static final String EXCHANGE_NAME = "NASDAQ 100";

    private final IndividualRiskRepository individualRiskRepository;
    private final InvestmentRepository investmentRepository;

    private final DecisionRowConverter converter;
    private final InvestmentRecommender investmentRecommender;

    public RiskManagementFacade(IndividualRiskRepository individualRiskRepository, InvestmentRepository investmentRepository,
                                DecisionRowConverter converter, InvestmentRecommender investmentRecommender) {
        this.individualRiskRepository = individualRiskRepository;
        this.investmentRepository = investmentRepository;
        this.converter = converter;
        this.investmentRecommender = investmentRecommender;
    }

    public RiskResults doRiskManagements() {
        List<RiskResult> riskResults = new ArrayList<>();

        Iterable<IndividualRisk> individualRisks = individualRiskRepository.findAll();
        individualRisks.forEach(individualRisk -> {
                    List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
                    List<Investment> updatedInvestments = updateNotionalSalesPrice(investments);

                    RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);
                    riskResults.add(riskManagementCalculator.toApi());
                }
        );

        return new RiskResults(riskResults);
    }

    public RiskResult doRiskManagement(Long id) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = updateNotionalSalesPrice(investments);

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);
        RiskResult riskResult = riskManagementCalculator.toApi();

        // TODO: Not sure should be done here?
        // List<SellRecommendation> sellRecommendations = doSellRecommendations();
        List<SaleRecommendation> saleRecommendations = investmentRecommender.getSaleRecommendations(riskManagementCalculator);
        if (!saleRecommendations.isEmpty()) {
            saleRecommendations.forEach(sellRecommendation ->
                    riskResult.getInvestments().stream()
                            .filter(investment -> sellRecommendation.getWkn().equalsIgnoreCase(investment.getWkn()))
                            .findFirst()
                            .ifPresent(investment -> investment.setSaleRecommendation(sellRecommendation))
            );
        }

        return riskResult;
    }

    public List<SaleRecommendation> doSaleRecommendations(Long id) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = updateNotionalSalesPrice(investments);

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);

        return investmentRecommender.getSaleRecommendations(riskManagementCalculator);
    }

    public SaleRecommendation doSaleRecommendation(Long id, Long investmentId) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        Investment investment = investments.stream().filter(i -> i.getId().equals(investmentId)).findFirst().orElseThrow();
        List<Investment> updatedInvestments = updateNotionalSalesPrice(Collections.singletonList(investment));

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);
        List<SaleRecommendation> saleRecommendations = investmentRecommender.getSaleRecommendations(riskManagementCalculator);
        return saleRecommendations.get(0);
    }

    public List<PurchaseRecommendation> doPurchaseRecommendations(Long id) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = updateNotionalSalesPrice(investments);

        return new ArrayList<>(investmentRecommender.getPurchaseRecommendations(
                new RiskManagementCalculator(individualRisk, updatedInvestments)));
    }

    public List<Investment> updateNotionalSalesPrice(List<Investment> investments) {
        List<ExchangeResult> exchangeResults = converter.fetchTable();
        final double exchangeRsl = findExchangeRsl(exchangeResults);

        investments.forEach(
                investment -> exchangeResults.stream()
                        .filter(row -> row.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .findFirst()
                        .ifPresent(result -> {
                            log.info("Calculate notional sales price for {}", result.getName());
                            investment.setCurrentNotionalSalesPrice(
                                    calculateNotionalSalesPrice(result.getRsl(), result.getPrice(), exchangeRsl));
                        })
        );

        return investments;
    }

    private double findExchangeRsl(List<ExchangeResult> rows) {
        return rows.stream()
                .filter(row -> EXCHANGE_NAME.equals(row.getName()))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

}
