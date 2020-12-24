package com.example.risk.control;

import com.example.risk.boundary.api.BuyRecommendation;
import com.example.risk.boundary.api.RiskManagementResult;
import com.example.risk.boundary.api.RiskManagementResultList;
import com.example.risk.boundary.api.SellRecommendation;
import com.example.risk.control.invest.InvestmentRecommender;
import com.example.risk.control.management.ManagementFacade;
import com.example.risk.control.management.RiskManagement;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.IndividualRiskRepository;
import com.example.risk.data.Investment;
import com.example.risk.data.InvestmentRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class Facade {

    private final IndividualRiskRepository individualRiskRepository;

    private final InvestmentRepository investmentRepository;

    private final ManagementFacade managementFacade;
    private final InvestmentRecommender investmentRecommender;

    private RiskManagement riskManagement;

    public Facade(IndividualRiskRepository individualRiskRepository, InvestmentRepository investmentRepository,
                  ManagementFacade managementFacade, InvestmentRecommender investmentRecommender) {
        this.individualRiskRepository = individualRiskRepository;
        this.investmentRepository = investmentRepository;
        this.managementFacade = managementFacade;
        this.investmentRecommender = investmentRecommender;
    }

    public void useFirst() {
        IndividualRisk individualRisk = individualRiskRepository.findAll().iterator().next();
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());

        riskManagement = new RiskManagement(individualRisk);
        riskManagement.setInvestments(investments);
    }

    public void useSecond() {
        Iterator<IndividualRisk> iterator = individualRiskRepository.findAll().iterator();
        IndividualRisk individualRisk = iterator.next();
        while (iterator.hasNext()) {
            individualRisk = iterator.next();
        }
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());

        riskManagement = new RiskManagement(individualRisk);
        riskManagement.setInvestments(investments);
    }

    public RiskManagementResult doRiskManagement() {
        riskManagement.setInvestments(managementFacade.updateNotionalSalesPrice(riskManagement.getInvestments()));
        return riskManagement.toApi();
    }

    public RiskManagementResultList doRiskManagements() {
        List<RiskManagementResult> riskManagementResults = new ArrayList<>();

        Iterable<IndividualRisk> individualRisks = individualRiskRepository.findAll();
        individualRisks.forEach(individualRisk -> {
                    List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());
                    List<Investment> updatedInvestments = managementFacade.updateNotionalSalesPrice(investments);

                    RiskManagement riskManagement = new RiskManagement(individualRisk);
                    riskManagement.setInvestments(updatedInvestments);
                    riskManagementResults.add(riskManagement.toApi());
                }

        );

        return new RiskManagementResultList(riskManagementResults);
    }

    public RiskManagementResult doRiskManagement(Long id) {
        IndividualRisk individualRisk = individualRiskRepository.findById(id).orElseThrow();
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(individualRisk.getId());

        RiskManagement riskManagement = new RiskManagement(individualRisk);
        riskManagement.setInvestments(managementFacade.updateNotionalSalesPrice(investments));

        return riskManagement.toApi();
    }

    public List<SellRecommendation> doSellRecommendations() {
        return investmentRecommender.getSellRecommendations(riskManagement);
    }

    public List<BuyRecommendation> doBuyRecommendations() {
        return investmentRecommender.getBuyRecommendations(riskManagement);
    }

}
