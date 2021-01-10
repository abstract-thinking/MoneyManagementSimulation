package com.example.risk.control;

import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.RiskResults;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.control.invest.InvestmentRecommender;
import com.example.risk.control.management.InvestmentFacade;
import com.example.risk.control.management.RiskManagementCalculator;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.IndividualRiskRepository;
import com.example.risk.data.Investment;
import com.example.risk.data.InvestmentRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
public class RiskManagementFacade {

    private final IndividualRiskRepository individualRiskRepository;
    private final InvestmentRepository investmentRepository;

    private final InvestmentFacade investmentFacade;
    private final InvestmentRecommender investmentRecommender;

    private boolean useFirst;

    public RiskManagementFacade(IndividualRiskRepository individualRiskRepository, InvestmentRepository investmentRepository,
                                InvestmentFacade investmentFacade, InvestmentRecommender investmentRecommender) {
        this.individualRiskRepository = individualRiskRepository;
        this.investmentRepository = investmentRepository;
        this.investmentFacade = investmentFacade;
        this.investmentRecommender = investmentRecommender;
    }

    public void useFirst() {
        useFirst = true;
    }

    public void useSecond() {
        useFirst = false;
    }

    public RiskResult doRiskManagement() {
        IndividualRisk individualRisk;
        if (useFirst) {
            individualRisk = individualRiskRepository.findAll().iterator().next();
        } else {
            Iterator<IndividualRisk> iterator = individualRiskRepository.findAll().iterator();
            individualRisk = iterator.next();
            while (iterator.hasNext()) {
                individualRisk = iterator.next();
            }
        }

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = investmentFacade.updateNotionalSalesPrice(investments);


        return new RiskManagementCalculator(individualRisk, updatedInvestments).toApi();
    }

    public RiskResults doRiskManagements() {
        List<RiskResult> riskResults = new ArrayList<>();

        Iterable<IndividualRisk> individualRisks = individualRiskRepository.findAll();
        individualRisks.forEach(individualRisk -> {
                    List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
                    List<Investment> updatedInvestments = investmentFacade.updateNotionalSalesPrice(investments);

                    RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);
                    riskResults.add(riskManagementCalculator.toApi());
                }
        );

        return new RiskResults(riskResults);
    }

    public RiskResult doRiskManagement(Long id) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = investmentFacade.updateNotionalSalesPrice(investments);

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

    public List<SaleRecommendation> doSellRecommendations() {
        IndividualRisk individualRisk;
        if (useFirst) {
            individualRisk = individualRiskRepository.findAll().iterator().next();
        } else {
            Iterator<IndividualRisk> iterator = individualRiskRepository.findAll().iterator();
            individualRisk = iterator.next();
            while (iterator.hasNext()) {
                individualRisk = iterator.next();
            }
        }

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = investmentFacade.updateNotionalSalesPrice(investments);

        return investmentRecommender.getSaleRecommendations(
                new RiskManagementCalculator(individualRisk, updatedInvestments));
    }

    public List<SaleRecommendation> doSaleRecommendations(Long id) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = investmentFacade.updateNotionalSalesPrice(investments);

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);

        return investmentRecommender.getSaleRecommendations(riskManagementCalculator);
    }

    public SaleRecommendation doSaleRecommendation(Long id, Long investmentId) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        Investment investment = investments.stream().filter(i -> i.getId().equals(investmentId)).findFirst().orElseThrow();
        List<Investment> updatedInvestments = investmentFacade.updateNotionalSalesPrice(Collections.singletonList(investment));

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);
        List<SaleRecommendation> saleRecommendations = investmentRecommender.getSaleRecommendations(riskManagementCalculator);
        return saleRecommendations.get(0);
    }

    public List<PurchaseRecommendation> doPurchaseRecommendations() {
        IndividualRisk individualRisk;
        if (useFirst) {
            individualRisk = individualRiskRepository.findAll().iterator().next();
        } else {
            Iterator<IndividualRisk> iterator = individualRiskRepository.findAll().iterator();
            individualRisk = iterator.next();
            while (iterator.hasNext()) {
                individualRisk = iterator.next();
            }
        }

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = investmentFacade.updateNotionalSalesPrice(investments);

        return new ArrayList<>(investmentRecommender.getPurchaseRecommendations(
                new RiskManagementCalculator(individualRisk, updatedInvestments)));
    }

    public List<PurchaseRecommendation> doPurchaseRecommendations(Long id) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        List<Investment> updatedInvestments = investmentFacade.updateNotionalSalesPrice(investments);

        return new ArrayList<>(investmentRecommender.getPurchaseRecommendations(
                new RiskManagementCalculator(individualRisk, updatedInvestments)));
    }


}
