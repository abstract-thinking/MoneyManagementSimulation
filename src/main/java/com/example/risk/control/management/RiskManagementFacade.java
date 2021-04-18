package com.example.risk.control.management;

import com.example.risk.boundary.api.CalculationResult;
import com.example.risk.boundary.api.CurrentDataResult;
import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendationMetadata;
import com.example.risk.boundary.api.RiskData;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.RiskResults;
import com.example.risk.boundary.api.SalesRecommendationMetadata;
import com.example.risk.control.management.calculate.CurrentDataProcessor;
import com.example.risk.control.management.calculate.InvestmentRecommender;
import com.example.risk.control.management.calculate.PositionCalculator;
import com.example.risk.control.management.calculate.RiskManagementCalculator;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import com.example.risk.data.InvestmentRepository;
import com.example.risk.data.RiskManagementRepository;
import com.example.risk.service.finanztreff.DecisionRowConverter;
import com.example.risk.service.finanztreff.ExchangeSnapshot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@Component
public class RiskManagementFacade {

    private final RiskManagementRepository riskManagementRepository;
    private final InvestmentRepository investmentRepository;
    private final DecisionRowConverter converter;

    public RiskManagementFacade(RiskManagementRepository riskManagementRepository,
                                InvestmentRepository investmentRepository, DecisionRowConverter converter) {
        this.riskManagementRepository = riskManagementRepository;
        this.investmentRepository = investmentRepository;
        this.converter = converter;
    }

    public RiskResults doRiskManagements() {
        final List<RiskResult> riskResults = new ArrayList<>();

        riskManagementRepository.findAll().forEach(risk -> {
                    final List<Investment> investments = investmentRepository.findAllByRiskManagementId(risk.getId());
                    final RiskManagementCalculator riskManagementCalculator =
                            new RiskManagementCalculator(risk, investments, fetchExchangeData());
                    riskResults.add(riskManagementCalculator.calculatePositionRisk());
                }
        );

        return new RiskResults(riskResults);
    }

    public RiskResult doRiskManagement(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());

        final RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, investments, fetchExchangeData());
        final RiskResult riskResult = riskManagementCalculator.calculatePositionRisk();

        new InvestmentRecommender(fetchExchangeData())
                .findSaleRecommendations(investments)
                .getSaleRecommendations().forEach(sellRecommendation ->
                riskResult.getInvestments().stream()
                        .filter(investment -> sellRecommendation.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .findFirst()
                        .ifPresent(investment -> investment.setSaleRecommendation(sellRecommendation))
        );

        return riskResult;
    }

    public SalesRecommendationMetadata doSaleRecommendations(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());

        return new InvestmentRecommender(fetchExchangeData()).findSaleRecommendations(investments);
    }

    public SalesRecommendationMetadata doSaleRecommendation(Long riskManagementId, Long investmentId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());
        final Investment investment = investments.stream()
                .filter(i -> i.getId().equals(investmentId))
                .findFirst()
                .orElseThrow();

        return new InvestmentRecommender(fetchExchangeData()).findSaleRecommendations(singletonList(investment));
    }

    public PurchaseRecommendationMetadata doPurchaseRecommendations(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        final PurchaseRecommendationMetadata purchaseRecommendations = new InvestmentRecommender(fetchExchangeData())
                .findPurchaseRecommendations(individualRisk);

        removeIfAlreadyInvested(individualRisk, purchaseRecommendations);

        return purchaseRecommendations;
    }

    public PurchaseRecommendation doPurchaseRecommendation(Long riskManagementId, String wkn) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        return new InvestmentRecommender(fetchExchangeData())
                .findPurchaseRecommendations(individualRisk)
                .getPurchaseRecommendations().stream()
                .filter(purchaseRecommendation -> purchaseRecommendation.getWkn().equalsIgnoreCase(wkn))
                .findFirst()
                .orElseThrow();
    }

    private ExchangeSnapshot fetchExchangeData() {
        return converter.fetchTable();
    }

    private void removeIfAlreadyInvested(IndividualRisk individualRisk, PurchaseRecommendationMetadata purchaseRecommendations) {
        investmentRepository.findAllByRiskManagementId(individualRisk.getId()).forEach(investment ->
                purchaseRecommendations.getPurchaseRecommendations().removeIf(recommendation ->
                        recommendation.getWkn().equalsIgnoreCase(investment.getWkn())));
    }

    public InvestmentResult doCreateInvestment(Long riskManagementId, InvestmentResult newInvestment) {
        final Investment investment = investmentRepository.save(toInvestment(riskManagementId, newInvestment));

        return InvestmentResult.builder()
                .id(investment.getId())
                .wkn(investment.getWkn())
                .quantity(investment.getQuantity())
                .purchasePrice(investment.getPurchasePrice())
                .notionalSalesPrice(investment.getStopPrice())
                .transactionCosts(investment.getTransactionCosts())
                .build();
    }

    private Investment toInvestment(Long riskManagementId, InvestmentResult newInvestment) {
        return Investment.builder()
                .wkn(newInvestment.getWkn())
                .name(newInvestment.getName())
                .quantity(newInvestment.getQuantity())
                .purchasePrice(newInvestment.getPurchasePrice())
                .stopPrice(newInvestment.getNotionalSalesPrice())
                .transactionCosts(newInvestment.getTransactionCosts())
                .riskManagementId(riskManagementId)
                .build();
    }

    public void doDeleteInvestment(Long investmentId) {
        investmentRepository.deleteById(investmentId);
    }

    public CalculationResult doPositionCalculation(long riskManagementId, String wkn) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        return new PositionCalculator(fetchExchangeData()).calculate(wkn, individualRisk);
    }

    public void doUpdateCoreData(Long riskManagementId, RiskData riskData) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        if (riskData.getTotalCapital() != null) {
            individualRisk.setTotalCapital(riskData.getTotalCapital());
        }

        if (!Double.isNaN(riskData.getIndividualPositionRiskInPercent())) {
            individualRisk.setIndividualPositionRiskInPercent(riskData.getIndividualPositionRiskInPercent());
        }

        riskManagementRepository.save(individualRisk);
    }

    public CurrentDataResult doCurrent(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());


        return new CurrentDataProcessor(fetchExchangeData()).process(investments);
    }
}
