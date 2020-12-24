package com.example.mm.control.management;


import com.example.mm.boundary.RiskManagementResult;
import com.example.mm.control.invest.ExchangeResult;
import com.example.mm.converter.DecisionRowConverter;
import com.example.mm.data.Investment;
import com.example.mm.data.InvestmentRepository;
import com.example.mm.data.MoneyManagement;
import com.example.mm.data.MoneyManagementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.example.mm.control.management.PriceCalculator.calculateNotionalSalesPrice;

@Slf4j
@Component
public class ManagementController {

    private static final String EXCHANGE_NAME = "NASDAQ 100";

    private final MoneyManagementRepository moneyManagementRepository;

    private final InvestmentRepository investmentRepository;

    private final DecisionRowConverter converter;

    private RiskManagement riskManagement;

    public ManagementController(MoneyManagementRepository moneyManagementRepository,
                                InvestmentRepository investmentRepository, DecisionRowConverter converter) {
        this.moneyManagementRepository = moneyManagementRepository;
        this.investmentRepository = investmentRepository;
        this.converter = converter;
    }

    public void updateInvestments(List<Investment> investments) {
        List<ExchangeResult> exchangeResults = converter.fetchTable();

        final double exchangeRsl = getRsl(exchangeResults);
        for (Investment investment : investments) {
            Optional<ExchangeResult> foundDecisionRow = exchangeResults.stream()
                    .filter(row -> row.getWkn().equalsIgnoreCase(investment.getWkn()))
                    .findFirst();

            if (foundDecisionRow.isPresent()) {
                ExchangeResult result = foundDecisionRow.get();
                investment.setUpdatedNotionalSalesPrice(
                        calculateNotionalSalesPrice(result.getRsl(), result.getPrice(), exchangeRsl));
            }
        }
    }

    private double getRsl(List<ExchangeResult> rows) {
        return rows.stream()
                .filter(row -> EXCHANGE_NAME.equals(row.getName()))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

    public RiskManagementResult getMoneyManagement() {
        MoneyManagement moneyManagement = moneyManagementRepository.findAll().iterator().next();

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(moneyManagement.getId());
        updateInvestments(investments);

        riskManagement = new RiskManagement(moneyManagement.getTotalCapital(),
                moneyManagement.getIndividualPositionRiskInPercent());
        riskManagement.setInvestments(investments);

        return riskManagement.toApi();
    }

    public RiskManagementResult getMoneyManagement2() {
        Iterator<MoneyManagement> iterator = moneyManagementRepository.findAll().iterator();
        MoneyManagement moneyManagement = iterator.next();
        while (iterator.hasNext()) {
            moneyManagement = iterator.next();
        }

        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(moneyManagement.getId());
        updateInvestments(investments);

        riskManagement = new RiskManagement(moneyManagement.getTotalCapital(),
                moneyManagement.getIndividualPositionRiskInPercent());
        riskManagement.setInvestments(investments);

        return riskManagement.toApi();
    }

}

