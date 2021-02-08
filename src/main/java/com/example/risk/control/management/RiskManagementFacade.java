package com.example.risk.control.management;

import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.RiskResults;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.control.management.caclulate.InvestmentRecommender;
import com.example.risk.control.management.caclulate.RiskManagementCalculator;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.IndividualRiskRepository;
import com.example.risk.data.Investment;
import com.example.risk.data.InvestmentRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class RiskManagementFacade {

    private final IndividualRiskRepository individualRiskRepository;
    private final InvestmentRepository investmentRepository;
    private final InvestmentRecommender investmentRecommender;

    public RiskManagementFacade(IndividualRiskRepository individualRiskRepository, InvestmentRepository investmentRepository,
            RiskManagementFacade investmentFacade, InvestmentRecommender investmentRecommender) {
        this.individualRiskRepository = individualRiskRepository;
        this.investmentRepository = investmentRepository;
        this.investmentRecommender = investmentRecommender;
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

        // TODO: Not sure should be done here?
        List<SaleRecommendation> saleRecommendations = investmentRecommender.getSaleRecommendations(riskManagementCalculator);
        if (saleRecommendations.isEmpty()) {
            return riskResult;
        }

        saleRecommendations.forEach(sellRecommendation ->
                riskResult.getInvestments().stream()
                        .filter(investment -> sellRecommendation.getWkn().equalsIgnoreCase(investment.getWkn()))
                        .findFirst()
                        .ifPresent(investment -> investment.setSaleRecommendation(sellRecommendation))
        );

        return riskResult;
    }

    public List<SaleRecommendation> doSaleRecommendations(Long id) {
        final IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = updateNotionalSalesPrice(investments);

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);

        return investmentRecommender.getSaleRecommendations(riskManagementCalculator);
    }

    public SaleRecommendation doSaleRecommendation(Long id, Long investmentId) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
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

    private List<Investment> updateNotionalSalesPrice(List<Investment> investments) {
        // TODO: logic lost
        return investments;
    }
}
