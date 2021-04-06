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
import com.example.risk.converter.ExchangeData;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import com.example.risk.data.InvestmentRepository;
import com.example.risk.data.RiskManagementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.risk.control.management.caclulate.InvestmentRecommender.EXCHANGE_NAME;
import static com.example.risk.control.management.caclulate.InvestmentRecommender.EXCHANGE_TRANSACTION_COSTS;
import static com.example.risk.control.management.caclulate.MoneyManagement.calculateQuantity;
import static com.example.risk.control.management.caclulate.PriceCalculator.calculateNotionalSalesPrice;

@Slf4j
@Component
public class RiskManagementFacade {

    private final RiskManagementRepository riskManagementRepository;
    private final InvestmentRepository investmentRepository;
    private final InvestmentRecommender investmentRecommender;
    private final DecisionRowConverter converter;

    public RiskManagementFacade(RiskManagementRepository riskManagementRepository, InvestmentRepository investmentRepository,
                                InvestmentRecommender investmentRecommender, DecisionRowConverter converter) {
        this.riskManagementRepository = riskManagementRepository;
        this.investmentRepository = investmentRepository;
        this.investmentRecommender = investmentRecommender;
        this.converter = converter;
    }

    public RiskResults doRiskManagements() {
        List<RiskResult> riskResults = new ArrayList<>();

        Iterable<IndividualRisk> riskManagements = riskManagementRepository.findAll();
        riskManagements.forEach(risk -> {
                    List<Investment> investments = investmentRepository.findAllByRiskManagementId(risk.getId());

            RiskManagementCalculator riskManagementCalculator =
                    new RiskManagementCalculator(risk, investments, fetchExchangeData());
                    riskResults.add(riskManagementCalculator.calculate());
                }
        );

        return new RiskResults(riskResults);
    }

    public RiskResult doRiskManagement(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, investments, fetchExchangeData());
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

    public SalesRecommendationMetadata doSaleRecommendations(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, investments, fetchExchangeData());
        return investmentRecommender.getSaleRecommendations(riskManagementCalculator);
    }

    public SalesRecommendationMetadata doSaleRecommendation(Long riskManagementId, Long investmentId) {
        IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());
        Investment investment = investments.stream()
                .filter(i -> i.getId().equals(investmentId))
                .findFirst()
                .orElseThrow();

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, investments, fetchExchangeData());
        // TODO: FIXME
        return investmentRecommender.getSaleRecommendations(riskManagementCalculator);
    }

    public PurchaseRecommendationMetadata doPurchaseRecommendations(Long riskManagementId) {
        IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());

        PurchaseRecommendationMetadata purchaseRecommendations = investmentRecommender.getPurchaseRecommendations(
                new RiskManagementCalculator(individualRisk, investments, fetchExchangeData()));

        removeIfAlreadyInvested(investments, purchaseRecommendations);

        return purchaseRecommendations;
    }

    public PurchaseRecommendation doPurchaseRecommendation(Long riskManagementId, String wkn) {
        IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());

        PurchaseRecommendationMetadata purchaseRecommendations = investmentRecommender.getPurchaseRecommendations(
                new RiskManagementCalculator(individualRisk, investments, fetchExchangeData()));

        return purchaseRecommendations.getPurchaseRecommendations().stream()
                .filter(purchaseRecommendation -> purchaseRecommendation.getWkn().equalsIgnoreCase(wkn))
                .findFirst()
                .orElseThrow();
    }

    private List<ExchangeData> fetchExchangeData() {
        return converter.fetchTable();
    }

    private void removeIfAlreadyInvested(List<Investment> investments, PurchaseRecommendationMetadata purchaseRecommendations) {
        investments.forEach(inv -> purchaseRecommendations
                .getPurchaseRecommendations().removeIf(rec -> rec.getWkn().equalsIgnoreCase(inv.getWkn())));
    }

    public InvestmentResult doCreateInvestment(Long riskManagementId, InvestmentResult newInvestment) {
        Investment save = investmentRepository.save(toInvestment(riskManagementId, newInvestment));

        // TODO: Fixme
        return InvestmentResult.builder()
                .id(save.getId())
                .build();
    }

    private Investment toInvestment(Long riskManagementId, InvestmentResult newInvestment) {
        return Investment.builder()
                .wkn(newInvestment.getWkn())
                .name(newInvestment.getName())
                .quantity(newInvestment.getQuantity())
                .purchasePrice(newInvestment.getPurchasePrice())
                .notionalSalesPrice(newInvestment.getNotionalSalesPrice())
                .transactionCosts(EXCHANGE_TRANSACTION_COSTS)
                .riskManagementId(riskManagementId)
                .build();
    }

    public void doDeleteInvestment(Long investmentId) {
        investmentRepository.deleteById(investmentId);
    }

    private double findExchangeRsl(List<ExchangeData> results) {
        return results.stream()
                .filter(result -> result.getName().equals(EXCHANGE_NAME))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

    public CalculationResult doPositionCalculation(long riskManagementId, String wkn) {
        IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        List<ExchangeData> results = converter.fetchTable();
        final double exchangeRsl = findExchangeRsl(results);

        ExchangeData result = results.stream()
                .filter(row -> row.getWkn().equalsIgnoreCase(wkn))
                .findFirst()
                .orElseThrow();

        BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(result.getRsl(), result.getPrice(), exchangeRsl);

        Investment possibleInvestment = Investment.builder()
                .purchasePrice(result.getPrice())
                .notionalSalesPrice(notionalSalesPrice)
                .transactionCosts(EXCHANGE_TRANSACTION_COSTS)
                .build();

        int quantity = calculateQuantity(individualRisk.calculateIndivdualPositionRisk(), possibleInvestment);

        return CalculationResult.builder()
                .wkn(result.getWkn())
                .name(result.getName())
                .price(result.getPrice())
                .quantity(quantity)
                .notionalSalesPrice(notionalSalesPrice)
                .transactionCosts(EXCHANGE_TRANSACTION_COSTS)
                .rsl(result.getRsl())
                .exchangeRsl(exchangeRsl)
                .positionRisk(individualRisk.calculateIndivdualPositionRisk())
                .build();
    }

    public void doUpdateCoreData(Long riskManagementId, RiskData riskData) {
        IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        if (riskData.getTotalCapital() != null) {
            individualRisk.setTotalCapital(riskData.getTotalCapital());
        }

        if (!Double.isNaN(riskData.getIndividualPositionRiskInPercent())) {
            individualRisk.setIndividualPositionRiskInPercent(riskData.getIndividualPositionRiskInPercent());
        }

        riskManagementRepository.save(individualRisk);
    }
}
