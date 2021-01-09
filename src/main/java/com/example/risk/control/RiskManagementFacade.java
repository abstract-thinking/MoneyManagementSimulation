package com.example.risk.control;

import com.example.risk.boundary.api.BuyRecommendation;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.RiskResults;
import com.example.risk.boundary.api.SellRecommendation;
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
        List<SellRecommendation> sellRecommendations = investmentRecommender.getSellRecommendations(riskManagementCalculator);
        if (!sellRecommendations.isEmpty()) {
            sellRecommendations.forEach(sellRecommendation ->
                    riskResult.getInvestments().stream()
                            .filter(investment -> sellRecommendation.getWkn().equalsIgnoreCase(investment.getWkn()))
                            .findFirst()
                            .ifPresent(investment -> investment.setSellRecommendation(sellRecommendation))
            );
        }

        return riskResult;
    }

    public List<SellRecommendation> doSellRecommendations() {
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

        return investmentRecommender.getSellRecommendations(
                new RiskManagementCalculator(individualRisk, updatedInvestments));
    }

    public SellRecommendation doSellRecommendation(Long id, Long investmentId) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
        Investment investment = investments.stream().filter(i -> i.getId().equals(investmentId)).findFirst().orElseThrow();
        List<Investment> updatedInvestments = investmentFacade.updateNotionalSalesPrice(Collections.singletonList(investment));

        RiskManagementCalculator riskManagementCalculator = new RiskManagementCalculator(individualRisk, updatedInvestments);
        List<SellRecommendation> sellRecommendations = investmentRecommender.getSellRecommendations(riskManagementCalculator);
        return sellRecommendations.get(0);
    }


    public List<BuyRecommendation> doBuyRecommendations() {
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

        return new ArrayList<>(investmentRecommender.getBuyRecommendations(
                new RiskManagementCalculator(individualRisk, updatedInvestments)));
    }

}
