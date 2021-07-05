package com.example.risk.service;

import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.QueryResult;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Comparator.comparing;

@Service
@AllArgsConstructor
public class RiskManagementCalculator {

    private BigDecimal calculateDepotRisk(QueryResult queryResult, List<Investment> investments) {
        return investments.stream()
                .map(investment -> calculatePositionRisk(queryResult, investment))
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateMaxNotionalPrice(QueryResult queryResult, Investment investment) {
        return queryResult.getCompanyResults().stream()
                .filter(company -> company.getSymbol().equalsIgnoreCase(investment.getSymbol()))
                .findFirst()
                .map(company -> investment.getStopPrice().max(company.getCurrentStopPrice()))
                .orElseThrow(() -> new RuntimeException("Symbol " + investment.getSymbol() + " not found"));
    }

    private BigDecimal calculatePositionRisk(QueryResult queryResult, Investment investment) {
        final BigDecimal profitOrLoss = calculateNotionalRevenue(queryResult, investment)
                .subtract(investment.getInvestmentCapital());

        return profitOrLoss.min(ZERO).negate();
    }

    private double calculateDepotRiskInPercent(QueryResult queryResult, List<Investment> investments) {
        return calculateDepotRisk(queryResult, investments)
                .divide(calculateTotalNotionalRevenue(queryResult, investments), 4, RoundingMode.DOWN)
                .movePointRight(2)
                .doubleValue();
    }

    private double calculateTotalRiskInPercent(QueryResult queryResult, IndividualRisk individualRisk, List<Investment> investments) {
        return calculateDepotRisk(queryResult, investments)
                .divide(calculateCurrentCapital(queryResult, individualRisk, investments), 4, RoundingMode.DOWN)
                .movePointRight(2)
                .doubleValue();
    }

    private BigDecimal calculateCurrentCapital(QueryResult queryResult, IndividualRisk individualRisk, List<Investment> investments) {
        return individualRisk.getTotalCapital()
                .subtract(calculateTotalInvestment(investments))
                .add(calculateTotalNotionalRevenue(queryResult, investments));
    }

    private BigDecimal calculateTotalInvestment(List<Investment> investments) {
        return investments.stream()
                .map(Investment::getInvestmentCapital)
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalNotionalRevenue(QueryResult queryResult, List<Investment> investments) {
        return investments.stream()
                .map(investment -> calculateNotionalRevenue(queryResult, investment))
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateNotionalRevenue(QueryResult queryResult, Investment investment) {
        return maxNotionalPrice(investment.getStopPrice(), calculateMaxNotionalPrice(queryResult, investment))
                .multiply(BigDecimal.valueOf(investment.getQuantity()))
                .subtract(investment.getTransactionCosts());
    }

    private BigDecimal maxNotionalPrice(BigDecimal stopPrice, BigDecimal notionalSalesPrice) {
        return stopPrice.max(notionalSalesPrice);
    }

    public RiskResult calculatePositionRisk(QueryResult queryResult, IndividualRisk individualRisk, List<Investment> investments) {
        return RiskResult.builder()
                .id(individualRisk.getId())
                .totalCapital(individualRisk.getTotalCapital())
                .name(individualRisk.getName())
                .individualPositionRiskInPercent(individualRisk.getIndividualPositionRiskInPercent())
                .individualPositionRisk(individualRisk.calculateIndividualPositionRisk())
                .investments(calculatePositionRisk(queryResult, investments))
                .totalInvestment(calculateTotalInvestment(investments))
                .totalRevenue(calculateTotalNotionalRevenue(queryResult, investments))
                .depotRisk(calculateDepotRisk(queryResult, investments))
                .depotRiskInPercent(calculateDepotRiskInPercent(queryResult, investments))
                .totalRiskInPercent(calculateTotalRiskInPercent(queryResult, individualRisk, investments))
                .build();
    }

    private List<InvestmentResult> calculatePositionRisk(QueryResult queryResult, List<Investment> investments) {
        List<InvestmentResult> investmentResults = new ArrayList<>(investments.size());
        investments.forEach(investment -> investmentResults.add(calculateResult(queryResult, investment)));

        investmentResults.sort(comparing(InvestmentResult::getPositionRisk));

        return investmentResults;
    }

    private InvestmentResult calculateResult(QueryResult queryResult, Investment investment) {
        return InvestmentResult.builder()
                .id(investment.getId())
                .symbol(investment.getSymbol())
                .name(investment.getName())
                .quantity(investment.getQuantity())
                .purchasePrice(investment.getPurchasePrice())
                .notionalSalesPrice(maxNotionalPrice(investment.getStopPrice(),
                        calculateMaxNotionalPrice(queryResult, investment)))
                .transactionCosts(investment.getTransactionCosts())
                .investment(investment.getInvestmentCapital())
                .notionalRevenue(calculateNotionalRevenue(queryResult, investment))
                .positionRisk(calculatePositionRisk(queryResult, investment))
                .build();
    }

}
