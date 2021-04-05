package com.example.risk.control.management;

import com.example.risk.boundary.api.CalculationResult;
import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendationMetadata;
import com.example.risk.boundary.api.RiskData;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.RiskResults;
import com.example.risk.boundary.api.SalesRecommendationMetadata;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.risk.control.management.caclulate.InvestmentRecommender.EXCHANGE_NAME;
import static com.example.risk.control.management.caclulate.InvestmentRecommender.EXCHANGE_TRANSACTION_COSTS;
import static com.example.risk.control.management.caclulate.MoneyManagement.calculateQuantity;
import static com.example.risk.control.management.caclulate.PriceCalculator.calculateNotionalSalesPrice;

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

                    RiskManagementCalculator riskManagementCalculator =
                            new RiskManagementCalculator(individualRisk, updatedInvestments);
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

    public InvestmentResult doCreateInvestment(Long riskId, InvestmentResult newInvestment) {
        return investmentRepository.save(toInvestment(riskId, newInvestment)).toApi();
    }

    private Investment toInvestment(Long riskId, InvestmentResult newInvestment) {
        return Investment.builder()
                .wkn(newInvestment.getWkn())
                .name(newInvestment.getName())
                .quantity(newInvestment.getQuantity())
                .currentPrice(newInvestment.getPurchasePrice())
                .purchasePrice(newInvestment.getPurchasePrice())
                .notionalSalesPrice(newInvestment.getNotionalSalesPrice())
                .transactionCosts(EXCHANGE_TRANSACTION_COSTS)
                .moneyManagementId(riskId)
                .build();
    }

    public void doDeleteInvestment(Long id) {
        investmentRepository.deleteById(id);
    }

    private double findExchangeRsl(List<ExchangeResult> results) {
        return results.stream()
                .filter(result -> result.getName().equals(EXCHANGE_NAME))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

    public CalculationResult doPositionCalculation(long id, String wkn) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();

        List<ExchangeResult> results = converter.fetchTable();
        final double exchangeRsl = findExchangeRsl(results);

        ExchangeResult result = results.stream()
                .filter(row -> row.getWkn().equalsIgnoreCase(wkn))
                .findFirst()
                .orElseThrow();

        BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(result.getRsl(), result.getPrice(), exchangeRsl);

        Investment possibleInvestment = Investment.builder()
                .purchasePrice(result.getPrice())
                .notionalSalesPrice(notionalSalesPrice)
                .transactionCosts(EXCHANGE_TRANSACTION_COSTS)
                .build();

        int quantity = calculateQuantity(individualRisk.calculatePositionRisk(), possibleInvestment);

        return CalculationResult.builder()
                .wkn(result.getWkn())
                .name(result.getName())
                .price(result.getPrice())
                .quantity(quantity)
                .notionalSalesPrice(notionalSalesPrice)
                .transactionCosts(EXCHANGE_TRANSACTION_COSTS)
                .rsl(result.getRsl())
                .exchangeRsl(exchangeRsl)
                .positionRisk(individualRisk.calculatePositionRisk())
                .build();
    }

    public void doUpdateCoreData(Long id, RiskData riskData) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();

        if (riskData.getTotalCapital() != null) {
            individualRisk.setTotalCapital(riskData.getTotalCapital());
        }

        if (!Double.isNaN(riskData.getIndividualPositionRiskInPercent())) {
            individualRisk.setIndividualPositionRiskInPercent(riskData.getIndividualPositionRiskInPercent());
        }

        individualRiskRepository.save(individualRisk);
    }
}
