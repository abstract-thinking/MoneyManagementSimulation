package com.example.mm.control;

import com.example.mm.boundary.BuyRecommendation;
import com.example.mm.boundary.RiskManagementResult;
import com.example.mm.boundary.SellRecommendation;
import com.example.mm.control.invest.InvestmentRecommender;
import com.example.mm.control.management.ManagementFacade;
import com.example.mm.control.management.RiskManagement;
import com.example.mm.data.Investment;
import com.example.mm.data.InvestmentRepository;
import com.example.mm.data.MoneyManagement;
import com.example.mm.data.MoneyManagementRepository;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class Facade {

    private final MoneyManagementRepository moneyManagementRepository;

    private final InvestmentRepository investmentRepository;

    private final ManagementFacade managementFacade;
    private final InvestmentRecommender investmentRecommender;

    private RiskManagement riskManagement;

    public Facade(MoneyManagementRepository moneyManagementRepository, InvestmentRepository investmentRepository,
                  ManagementFacade managementFacade, InvestmentRecommender investmentRecommender) {
        this.moneyManagementRepository = moneyManagementRepository;
        this.investmentRepository = investmentRepository;
        this.managementFacade = managementFacade;
        this.investmentRecommender = investmentRecommender;
    }

    public void useFirst() {
        MoneyManagement moneyManagement = moneyManagementRepository.findAll().iterator().next();
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(moneyManagement.getId());

        riskManagement = new RiskManagement(moneyManagement.getTotalCapital(),
                moneyManagement.getIndividualPositionRiskInPercent());
        riskManagement.setInvestments(investments);
    }

    public void useSecond() {
        Iterator<MoneyManagement> iterator = moneyManagementRepository.findAll().iterator();
        MoneyManagement moneyManagement = iterator.next();
        while (iterator.hasNext()) {
            moneyManagement = iterator.next();
        }
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(moneyManagement.getId());

        riskManagement = new RiskManagement(moneyManagement.getTotalCapital(),
                moneyManagement.getIndividualPositionRiskInPercent());
        riskManagement.setInvestments(investments);
    }

    public RiskManagementResult getMoneyManagement() {
        riskManagement.setInvestments(managementFacade.updateNotionalSalesPrice(riskManagement.getInvestments()));
        return riskManagement.toApi();
    }

    public List<SellRecommendation> getSellRecommendations() {
        return investmentRecommender.getSellRecommendations(riskManagement);
    }

    public List<BuyRecommendation> getBuyRecommendations() {
        // TODO: Add money management
        return investmentRecommender.getBuyRecommendations();
    }
}
