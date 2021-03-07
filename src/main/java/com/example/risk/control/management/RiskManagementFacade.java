package com.example.risk.control.management;

import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendationMetadata;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.RiskResults;
import com.example.risk.boundary.api.SalesRecommendationMetadata;
import com.example.risk.boundary.api.SearchResult;
import com.example.risk.control.management.caclulate.InvestmentRecommender;
import com.example.risk.control.management.caclulate.RiskManagementCalculator;
import com.example.risk.converter.DecisionRowConverter;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.IndividualRiskRepository;
import com.example.risk.data.Investment;
import com.example.risk.data.InvestmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class RiskManagementFacade {

    private final IndividualRiskRepository individualRiskRepository;
    private final InvestmentRepository investmentRepository;
    private final InvestmentRecommender investmentRecommender;
    private final DecisionRowConverter converter;

    public RiskManagementFacade(IndividualRiskRepository individualRiskRepository, InvestmentRepository investmentRepository,
                                InvestmentRecommender investmentRecommender, DecisionRowConverter converter) {
        this.individualRiskRepository = individualRiskRepository;
        this.investmentRepository = investmentRepository;
        this.investmentRecommender = investmentRecommender;
        this.converter = converter;
    }

    public RiskResults doRiskManagements() {
        List<RiskResult> riskResults = new ArrayList<>();

        Iterable<IndividualRisk> individualRisks = individualRiskRepository.findAll();
        individualRisks.forEach(individualRisk -> {
                    List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
                    List<Investment> updatedInvestments = updateNotionalSalesPrice(investments);

                    RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);
                    riskResults.add(riskManagementCalculator.calculate());
                }
        );

        return new RiskResults(riskResults);
    }

    public RiskResult doRiskManagement(Long id) {
        final IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = updateNotionalSalesPrice(investments);

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);
        RiskResult riskResult = riskManagementCalculator.calculate();

        SalesRecommendationMetadata saleRecommendations = investmentRecommender.getSaleRecommendations(riskManagementCalculator);
        if (saleRecommendations.getSaleRecommendations().isEmpty()) {
            return riskResult;
        }

        saleRecommendations.getSaleRecommendations().forEach(sellRecommendation ->
                riskResult.getInvestments().stream()
                        .filter(investment -> sellRecommendation.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .findFirst()
                        .ifPresent(investment -> investment.setSaleRecommendation(sellRecommendation))
        );

        return riskResult;
    }

    public SalesRecommendationMetadata doSaleRecommendations(Long id) {
        final IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = updateNotionalSalesPrice(investments);

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);
        return investmentRecommender.getSaleRecommendations(riskManagementCalculator);
    }

    public SalesRecommendationMetadata doSaleRecommendation(Long id, Long investmentId) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        Investment investment = investments.stream()
                .filter(i -> i.getId().equals(investmentId))
                .findFirst()
                .orElseThrow();
        List<Investment> updatedInvestments = updateNotionalSalesPrice(Collections.singletonList(investment));

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);
        return investmentRecommender.getSaleRecommendations(riskManagementCalculator);
    }

    public PurchaseRecommendationMetadata doPurchaseRecommendations(Long id) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = updateNotionalSalesPrice(investments);

        PurchaseRecommendationMetadata purchaseRecommendations = investmentRecommender.getPurchaseRecommendations(
                new RiskManagementCalculator(individualRisk, updatedInvestments));

        removeIfAlreadyInvested(investments, purchaseRecommendations);
        cutOffTail(purchaseRecommendations);

        return purchaseRecommendations;
    }

    public PurchaseRecommendation doPurchaseRecommendation(Long id, String wkn) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = updateNotionalSalesPrice(investments);

        PurchaseRecommendationMetadata purchaseRecommendations = investmentRecommender.getPurchaseRecommendations(
                new RiskManagementCalculator(individualRisk, updatedInvestments));

        return purchaseRecommendations.getPurchaseRecommendations().stream()
                .filter(purchaseRecommendation -> purchaseRecommendation.getWkn().equalsIgnoreCase(wkn))
                .findFirst()
                .orElseThrow();
    }

    private List<Investment> updateNotionalSalesPrice(List<Investment> investments) {
        investments.forEach(investment -> converter.fetchTable().stream()
                .filter(row -> row.getWkn().equalsIgnoreCase(investment.getWkn()))
                .findFirst()
                .ifPresent(result -> investment.setCurrentPrice(result.getPrice()))
        );

        return investments;
    }

    private void removeIfAlreadyInvested(List<Investment> investments, PurchaseRecommendationMetadata purchaseRecommendations) {
        investments.forEach(inv -> purchaseRecommendations
                .getPurchaseRecommendations().removeIf(rec -> rec.getWkn().equalsIgnoreCase(inv.getWkn())));
    }

    private void cutOffTail(PurchaseRecommendationMetadata metadata) {
        List<PurchaseRecommendation> purchaseRecommendations = metadata.getPurchaseRecommendations();
        if (purchaseRecommendations.size() < 7) {
            return;
        }

        purchaseRecommendations.subList(7, purchaseRecommendations.size()).clear();
    }

    public SearchResult doSearch(long id, String wkn) {
        RiskResult riskResult = doRiskManagement(id);
        Optional<InvestmentResult> foundInvestment = riskResult.getInvestments().stream()
                .filter(investmentResult -> investmentResult.getWkn().equalsIgnoreCase(wkn))
                .findFirst();
        if (foundInvestment.isPresent()) {
            InvestmentResult investmentResult = foundInvestment.get();
            return SearchResult.builder()
                    .id(investmentResult.getId())
                    .wkn(investmentResult.getWkn())
                    .name(investmentResult.getName())
                    .quantity(investmentResult.getQuantity())
                    .transactionCosts(investmentResult.getTransactionCosts())
                    .purchasePrice(investmentResult.getPurchasePrice())
                    .positionRisk(investmentResult.getPositionRisk())
                    .notionalRevenue(investmentResult.getNotionalRevenue())
                    .notionalSalesPrice(investmentResult.getNotionalSalesPrice())
                    .build();
        } else {
            PurchaseRecommendation purchaseRecommendation = doPurchaseRecommendation(id, wkn);

            return SearchResult.builder()
                    .wkn(purchaseRecommendation.getWkn())
                    .name(purchaseRecommendation.getName())
                    .quantity(purchaseRecommendation.getQuantity())
                    .notionalSalesPrice(purchaseRecommendation.getNotionalSalesPrice())
                    .build();
        }
    }
}
