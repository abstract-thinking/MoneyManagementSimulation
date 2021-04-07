package com.example.risk.control.management.caclulate;

import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.converter.ExchangeData;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.example.risk.control.management.caclulate.PriceCalculator.calculateNotionalSalesPrice;
import static java.math.BigDecimal.ZERO;

@AllArgsConstructor
public class RiskManagementCalculator {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final IndividualRisk individualRisk;

    private final List<Investment> investments;

    private final ExchangeSnapshot exchangeSnapshot;

    private BigDecimal calculateDepotRisk() {
        BigDecimal depotRisk = ZERO;
        for (Investment investment : investments) {
            final BigDecimal notionalPrice = calculateMaxNotionalPrice(investment);
            depotRisk = depotRisk.add(calculatePositionRisk(investment, notionalPrice));
        }

        return depotRisk;
    }

    private BigDecimal calculateMaxNotionalPrice(Investment investment) {
        for (ExchangeData exchangeData : exchangeSnapshot.getExchangeData()) {
            if (exchangeData.getWkn().equalsIgnoreCase(investment.getWkn())) {
                final BigDecimal notionalSalesPrice = calculateNotionalSalesPrice(exchangeData.getRsl(), exchangeData.getPrice(), exchangeSnapshot.getExchangeRsl());
                return investment.getStopPrice().max(notionalSalesPrice);
            }
        }

        throw new RuntimeException("WKN " + investment.getWkn() + " not found");
    }

    private BigDecimal calculatePositionRisk(Investment investment, BigDecimal notionalSalesPrice) {
        final BigDecimal notionalRevenue = calculateNotionalRevenue(investment, notionalSalesPrice);
        final BigDecimal profitOrLoss = notionalRevenue.subtract(investment.getInvestment());

        return profitOrLoss.min(ZERO).negate();
    }

    private double calculateDepotRiskInPercent() {
        return calculateDepotRisk()
                .divide(calculateTotalNotionalRevenue(), 4, RoundingMode.DOWN)
                .multiply(ONE_HUNDRED)
                .doubleValue();
    }

    private double calculateTotalRiskInPercent() {
        return calculateDepotRisk()
                .divide(individualRisk.getTotalCapital(), 4, RoundingMode.DOWN)
                .multiply(ONE_HUNDRED).doubleValue();
    }

    private BigDecimal calculateTotalInvestment() {
        return investments.stream()
                .map(Investment::getInvestment)
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalNotionalRevenue() {
        BigDecimal totalNotionalRevenue = ZERO;
        for (Investment investment : investments) {
            final BigDecimal notionalSalesPrice = calculateMaxNotionalPrice(investment);
            final BigDecimal notionalRevenue = calculateNotionalRevenue(investment, notionalSalesPrice);
            totalNotionalRevenue = totalNotionalRevenue.add(notionalRevenue);
        }

        return totalNotionalRevenue;
    }

    private BigDecimal calculateNotionalRevenue(Investment investment, BigDecimal notionalSalesPrice) {
        return notionalSalesPrice.multiply(BigDecimal.valueOf(investment.getQuantity()))
                .subtract(investment.getTransactionCosts());
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
        for (Investment investment : investments) {
            investmentResults.add(calculatePositionRisk(investment));
        }

        return investmentResults;
    }

    private InvestmentResult calculatePositionRisk(Investment investment) {
        final BigDecimal notionalPrice = calculateMaxNotionalPrice(investment);

        return InvestmentResult.builder()
                .id(investment.getId())
                .wkn(investment.getWkn())
                .name(investment.getName())
                .quantity(investment.getQuantity())
                .purchasePrice(investment.getPurchasePrice())
                .notionalSalesPrice(notionalPrice)
                .transactionCosts(investment.getTransactionCosts())
                .investment(investment.getInvestment())
                .notionalRevenue(calculateNotionalRevenue(investment, notionalPrice))
                .positionRisk(calculatePositionRisk(investment, notionalPrice))
                .build();
    }
}
