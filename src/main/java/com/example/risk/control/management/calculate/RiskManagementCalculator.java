package com.example.risk.control.management.calculate;

import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import com.example.risk.service.finanztreff.ExchangeSnapshot;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.example.risk.control.management.calculate.PriceCalculator.calculateNotionalSalesPrice;
import static java.math.BigDecimal.ZERO;
import static java.util.Comparator.comparing;

@AllArgsConstructor
public class RiskManagementCalculator {

    private final IndividualRisk individualRisk;

    private final List<Investment> investments;

    private final ExchangeSnapshot exchangeSnapshot;

    private BigDecimal calculateDepotRisk() {
        return investments.stream()
                .map(this::calculatePositionRisk)
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateMaxNotionalPrice(Investment investment) {
        return exchangeSnapshot.getQuotes().stream()
                .filter(data -> data.getWkn().equalsIgnoreCase(investment.getWkn()))
                .findFirst()
                .map(data -> calculateNotionalSalesPrice(data.getRsl(), data.getPrice(), exchangeSnapshot.getRsl()))
                .orElseThrow(() -> new RuntimeException("WKN " + investment.getWkn() + " not found"));
    }

    private BigDecimal calculatePositionRisk(Investment investment) {
        final BigDecimal profitOrLoss = calculateNotionalRevenue(investment).subtract(investment.getInvestment());

        return profitOrLoss.min(ZERO).negate();
    }

    private double calculateDepotRiskInPercent() {
        return calculateDepotRisk()
                .divide(calculateTotalNotionalRevenue(), 4, RoundingMode.DOWN)
                .movePointRight(2)
                .doubleValue();
    }

    private double calculateTotalRiskInPercent() {
        return calculateDepotRisk()
                .divide(calculateCurrentCapital(), 4, RoundingMode.DOWN)
                .movePointRight(2)
                .doubleValue();
    }

    private BigDecimal calculateCurrentCapital() {
        return individualRisk.getTotalCapital()
                .subtract(calculateTotalInvestment())
                .add(calculateTotalNotionalRevenue());
    }

    private BigDecimal calculateTotalInvestment() {
        return investments.stream()
                .map(Investment::getInvestment)
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalNotionalRevenue() {
        return investments.stream()
                .map(this::calculateNotionalRevenue)
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateNotionalRevenue(Investment investment) {
        return maxNotionalPrice(investment.getStopPrice(), calculateMaxNotionalPrice(investment))
                .multiply(BigDecimal.valueOf(investment.getQuantity()))
                .subtract(investment.getTransactionCosts());
    }

    private BigDecimal maxNotionalPrice(BigDecimal stopPrice, BigDecimal notionalSalesPrice) {
        return stopPrice.max(notionalSalesPrice);
    }

    public RiskResult calculatePositionRisk() {
        return RiskResult.builder()
                .id(individualRisk.getId())
                .totalCapital(individualRisk.getTotalCapital())
                .name(individualRisk.getName())
                .individualPositionRiskInPercent(individualRisk.getIndividualPositionRiskInPercent())
                .individualPositionRisk(individualRisk.calculateIndividualPositionRisk())
                .investments(calculatePositionRisk(investments))
                .totalInvestment(calculateTotalInvestment())
                .totalRevenue(calculateTotalNotionalRevenue())
                .depotRisk(calculateDepotRisk())
                .depotRiskInPercent(calculateDepotRiskInPercent())
                .totalRiskInPercent(calculateTotalRiskInPercent())
                .build();
    }

    private List<InvestmentResult> calculatePositionRisk(List<Investment> investments) {
        List<InvestmentResult> investmentResults = new ArrayList<>(investments.size());
        investments.forEach(investment -> investmentResults.add(calculateResult(investment)));

        investmentResults.sort(comparing(InvestmentResult::getPositionRisk));

        return investmentResults;
    }

    private InvestmentResult calculateResult(Investment investment) {
        return InvestmentResult.builder()
                .id(investment.getId())
                .wkn(investment.getWkn())
                .name(investment.getName())
                .quantity(investment.getQuantity())
                .purchasePrice(investment.getPurchasePrice())
                .notionalSalesPrice(maxNotionalPrice(investment.getStopPrice(), calculateMaxNotionalPrice(investment)))
                .transactionCosts(investment.getTransactionCosts())
                .investment(investment.getInvestment())
                .notionalRevenue(calculateNotionalRevenue(investment))
                .positionRisk(calculatePositionRisk(investment))
                .build();
    }
}
