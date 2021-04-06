package com.example.risk.control.management.caclulate;

import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.converter.ExchangeData;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.example.risk.control.management.caclulate.InvestmentRecommender.EXCHANGE_NAME;
import static com.example.risk.control.management.caclulate.PriceCalculator.calculateNotionalSalesPrice;
import static java.math.BigDecimal.ZERO;

public class RiskManagementCalculator {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final IndividualRisk individualRisk;

    @Getter
    private final List<Investment> investments;

    private final List<ExchangeData> exchangeData;

    private final double exchangeRsl;

    public RiskManagementCalculator(IndividualRisk individualRisk, List<Investment> investments, List<ExchangeData> exchangeData) {
        this.individualRisk = individualRisk;
        this.investments = investments;
        this.exchangeData = exchangeData;

        this.exchangeRsl = findExchangeRsl(exchangeData);
    }

    private double findExchangeRsl(List<ExchangeData> results) {
        return results.stream()
                .filter(result -> result.getName().equals(EXCHANGE_NAME))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

    public BigDecimal calculateIndividualPositionRisk() {
        return individualRisk.calculateIndivdualPositionRisk();
    }

    private BigDecimal calculateDepotRisk() {
        BigDecimal depotRisk = ZERO;
        for (Investment investment : investments) {
            BigDecimal notionalPrice = calculateNotionalPrice(investment.getWkn());
            depotRisk = depotRisk.add(calculatePositionRisk(investment, notionalPrice));
        }

        return depotRisk;
    }

    public BigDecimal calculatePositionRisk(Investment investment, BigDecimal notionalSalesPrice) {
        BigDecimal notionalRevenue = calculateNotionalRevenue(investment, notionalSalesPrice);
        final BigDecimal profitOrLoss = notionalRevenue.subtract(investment.getInvestment());
        return isLoss(profitOrLoss) ? profitOrLoss.negate() : BigDecimal.ZERO;
    }

    private boolean isLoss(BigDecimal profitOrLoss) {
        return profitOrLoss.compareTo(BigDecimal.ZERO) < 0;
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
            final BigDecimal notionalSalesPrice = calculateNotionalPrice(investment.getWkn());
            BigDecimal notionalRevenue = calculateNotionalRevenue(investment, notionalSalesPrice);
            totalNotionalRevenue = totalNotionalRevenue.add(notionalRevenue);
        }

        return totalNotionalRevenue;
    }

    private BigDecimal calculateNotionalRevenue(Investment investment, BigDecimal notionalSalesPrice) {
        return notionalSalesPrice.multiply(BigDecimal.valueOf(investment.getQuantity()))
                .subtract(investment.getTransactionCosts());
    }

    private BigDecimal calculateNotionalPrice(String wkn) {
        for (ExchangeData exchangeData : exchangeData) {
            if (exchangeData.getWkn().equalsIgnoreCase(wkn)) {
                return calculateNotionalSalesPrice(exchangeData.getRsl(), exchangeData.getPrice(), exchangeRsl);
            }
        }

        throw new RuntimeException("WKN " + wkn + " not found");
    }

    public RiskResult calculate() {
        return RiskResult.builder()
                .id(individualRisk.getId())
                .totalCapital(individualRisk.getTotalCapital())
                .name(individualRisk.getName())
                .individualPositionRiskInPercent(individualRisk.getIndividualPositionRiskInPercent())
                .individualPositionRisk(calculateIndividualPositionRisk())
                .investments(calculate(investments))
                .totalInvestment(calculateTotalInvestment())
                .totalRevenue(calculateTotalNotionalRevenue())
                .depotRisk(calculateDepotRisk())
                .depotRiskInPercent(calculateDepotRiskInPercent())
                .totalRiskInPercent(calculateTotalRiskInPercent())
                .build();
    }

    private List<InvestmentResult> calculate(List<Investment> investments) {
        List<InvestmentResult> investmentResults = new ArrayList<>(investments.size());
        for (Investment investment : investments) {
            BigDecimal notionalPrice = calculateNotionalPrice(investment.getWkn());

            investmentResults.add(InvestmentResult.builder()
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
                    .build());
        }

        return investmentResults;
    }


}
