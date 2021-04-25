package com.example.risk.control.management;

import com.example.risk.boundary.api.CalculationResult;
import com.example.risk.boundary.api.CurrentDataResult;
import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendations;
import com.example.risk.boundary.api.RiskData;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.RiskResults;
import com.example.risk.boundary.api.SalesRecommendations;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import com.example.risk.data.InvestmentRepository;
import com.example.risk.data.RiskManagementRepository;
import com.example.risk.service.CurrentDataProcessor;
import com.example.risk.service.InvestmentRecommender;
import com.example.risk.service.PositionCalculator;
import com.example.risk.service.RiskManagementCalculator;
import com.example.risk.service.finanztreff.DecisionRowConverter;
import com.example.risk.service.finanztreff.ExchangeSnapshot;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@AllArgsConstructor
@Component
public class RiskManagementFacade {

    private final RiskManagementRepository riskManagementRepository;

    private final InvestmentRepository investmentRepository;

    private final DecisionRowConverter converter;

    private final RiskManagementCalculator riskManagementCalculator;

    private final CurrentDataProcessor currentDataProcessor;

    private final InvestmentRecommender investmentRecommender;

    public RiskResults doRiskManagements() {
        final List<RiskResult> riskResults = new ArrayList<>();

        riskManagementRepository.findAll().forEach(risk -> {
                    final List<Investment> investments = investmentRepository.findAllByRiskManagementId(risk.getId());
                    riskResults.add(riskManagementCalculator.calculatePositionRisk(fetchExchangeData(), risk, investments));
                }
        );

        return new RiskResults(riskResults);
    }

    public RiskResult doRiskManagement(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());

        final RiskResult riskResult = riskManagementCalculator.calculatePositionRisk(fetchExchangeData(), individualRisk, investments);

        investmentRecommender.findSaleRecommendations(fetchExchangeData(), investments)
                .getSaleRecommendations().forEach(sellRecommendation ->
                riskResult.getInvestments().stream()
                        .filter(investment -> sellRecommendation.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .findFirst()
                        .ifPresent(investment -> investment.setSaleRecommendation(sellRecommendation))
        );

        return riskResult;
    }

    public SalesRecommendations doSaleRecommendations(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());

        return investmentRecommender.findSaleRecommendations(fetchExchangeData(), investments);
    }

    public SalesRecommendations doSaleRecommendation(Long riskManagementId, Long investmentId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());
        final Investment investment = investments.stream()
                .filter(i -> i.getId().equals(investmentId))
                .findFirst()
                .orElseThrow();

        return investmentRecommender.findSaleRecommendations(fetchExchangeData(), singletonList(investment));
    }

    public PurchaseRecommendations doPurchaseRecommendations(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        final PurchaseRecommendations purchaseRecommendations =
                investmentRecommender.findPurchaseRecommendations(fetchExchangeData(), individualRisk);

        removeIfAlreadyInvested(individualRisk, purchaseRecommendations);

        return purchaseRecommendations;
    }

    public PurchaseRecommendation doPurchaseRecommendation(Long riskManagementId, String wkn) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        return investmentRecommender.findPurchaseRecommendations(fetchExchangeData(), individualRisk)
                .getPurchaseRecommendations().stream()
                .filter(purchaseRecommendation -> purchaseRecommendation.getWkn().equalsIgnoreCase(wkn))
                .findFirst()
                .orElseThrow();
    }

    private ExchangeSnapshot fetchExchangeData() {
        return converter.fetchTable();
    }

    private void removeIfAlreadyInvested(IndividualRisk individualRisk, PurchaseRecommendations purchaseRecommendations) {
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

    @Autowired
    PositionCalculator positionCalculator;

    public CalculationResult doPositionCalculation(long riskManagementId, String wkn) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        return positionCalculator.calculate(fetchExchangeData(), wkn, individualRisk);
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

        return currentDataProcessor.process(fetchExchangeData(), investments);
    }

}
