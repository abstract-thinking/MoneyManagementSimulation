package com.example.risk.service;

import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.QueryResult;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import com.example.risk.service.finanztreff.ExchangeSnapshot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.example.risk.service.PriceCalculator.calculateNotionalSalesPrice;
import static java.math.BigDecimal.ZERO;
import static java.util.Comparator.comparing;

@Service
@AllArgsConstructor
public class RiskManagementCalculator {

    private BigDecimal calculateDepotRisk(ExchangeSnapshot exchangeSnapshot, List<Investment> investments) {
        return investments.stream()
                .map(investment -> calculatePositionRisk(exchangeSnapshot, investment))
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDepotRisk(QueryResult queryResult, List<Investment> investments) {
        return investments.stream()
                .map(investment -> calculatePositionRisk(queryResult, investment))
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateMaxNotionalPrice(ExchangeSnapshot snapshot, Investment investment) {
        return snapshot.getQuotes().stream()
                .filter(data -> data.getWkn().equalsIgnoreCase(investment.getWkn()))
                .findFirst()
                .map(data -> calculateNotionalSalesPrice(data.getRsl(), data.getPrice(), snapshot.getExchange().getRsl()))
                .orElseThrow(() -> new RuntimeException("WKN " + investment.getWkn() + " not found"));
    }

    private BigDecimal calculateMaxNotionalPrice(QueryResult queryResult, Investment investment) {
        return queryResult.getCompanyResults().stream()
                .filter(company -> company.getSymbol().equalsIgnoreCase(investment.getSymbol()))
                .findFirst()
                .map(company -> company.getStopPrice().max(company.getStopPrice()))
                .orElseThrow(() -> new RuntimeException("Symbol " + investment.getSymbol() + " not found"));
    }

    private BigDecimal calculatePositionRisk(ExchangeSnapshot exchangeSnapshot, Investment investment) {
        final BigDecimal profitOrLoss = calculateNotionalRevenue(exchangeSnapshot, investment)
                .subtract(investment.getInvestmentCapital());

        return profitOrLoss.min(ZERO).negate();
    }

    private BigDecimal calculatePositionRisk(QueryResult queryResult, Investment investment) {
        final BigDecimal profitOrLoss = calculateNotionalRevenue(queryResult, investment)
                .subtract(investment.getInvestmentCapital());

        return profitOrLoss.min(ZERO).negate();
    }

    private double calculateDepotRiskInPercent(ExchangeSnapshot exchangeSnapshot, List<Investment> investments) {
        return calculateDepotRisk(exchangeSnapshot, investments)
                .divide(calculateTotalNotionalRevenue(exchangeSnapshot, investments), 4, RoundingMode.DOWN)
                .movePointRight(2)
                .doubleValue();
    }

    private double calculateDepotRiskInPercent(QueryResult queryResult, List<Investment> investments) {
        return calculateDepotRisk(queryResult, investments)
                .divide(calculateTotalNotionalRevenue(queryResult, investments), 4, RoundingMode.DOWN)
                .movePointRight(2)
                .doubleValue();
    }

    private double calculateTotalRiskInPercent(ExchangeSnapshot exchangeSnapshot, IndividualRisk individualRisk, List<Investment> investments) {
        return calculateDepotRisk(exchangeSnapshot, investments)
                .divide(calculateCurrentCapital(exchangeSnapshot, individualRisk, investments), 4, RoundingMode.DOWN)
                .movePointRight(2)
                .doubleValue();
    }

    private double calculateTotalRiskInPercent(QueryResult queryResult, IndividualRisk individualRisk, List<Investment> investments) {
        return calculateDepotRisk(queryResult, investments)
                .divide(calculateCurrentCapital(queryResult, individualRisk, investments), 4, RoundingMode.DOWN)
                .movePointRight(2)
                .doubleValue();
    }

    private BigDecimal calculateCurrentCapital(ExchangeSnapshot exchangeSnapshot, IndividualRisk individualRisk, List<Investment> investments) {
        return individualRisk.getTotalCapital()
                .subtract(calculateTotalInvestment(investments))
                .add(calculateTotalNotionalRevenue(exchangeSnapshot, investments));
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

    private BigDecimal calculateTotalNotionalRevenue(ExchangeSnapshot exchangeSnapshot, List<Investment> investments) {
        return investments.stream()
                .map(investment -> calculateNotionalRevenue(exchangeSnapshot, investment))
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalNotionalRevenue(QueryResult queryResult, List<Investment> investments) {
        return investments.stream()
                .map(investment -> calculateNotionalRevenue(queryResult, investment))
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateNotionalRevenue(ExchangeSnapshot exchangeSnapshot, Investment investment) {
        return maxNotionalPrice(investment.getStopPrice(), calculateMaxNotionalPrice(exchangeSnapshot, investment))
                .multiply(BigDecimal.valueOf(investment.getQuantity()))
                .subtract(investment.getTransactionCosts());
    }

    private BigDecimal calculateNotionalRevenue(QueryResult queryResult, Investment investment) {
        return maxNotionalPrice(investment.getStopPrice(), calculateMaxNotionalPrice(queryResult, investment))
                .multiply(BigDecimal.valueOf(investment.getQuantity()))
                .subtract(investment.getTransactionCosts());
    }

    private BigDecimal maxNotionalPrice(BigDecimal stopPrice, BigDecimal notionalSalesPrice) {
        return stopPrice.max(notionalSalesPrice);
    }

    public RiskResult calculatePositionRisk(ExchangeSnapshot exchangeSnapshot, IndividualRisk individualRisk, List<Investment> investments) {
        return RiskResult.builder()
                .id(individualRisk.getId())
                .totalCapital(individualRisk.getTotalCapital())
                .name(individualRisk.getName())
                .individualPositionRiskInPercent(individualRisk.getIndividualPositionRiskInPercent())
                .individualPositionRisk(individualRisk.calculateIndividualPositionRisk())
                .investments(calculatePositionRisk2(exchangeSnapshot, investments))
                .totalInvestment(calculateTotalInvestment(investments))
                .totalRevenue(calculateTotalNotionalRevenue(exchangeSnapshot, investments))
                .depotRisk(calculateDepotRisk(exchangeSnapshot, investments))
                .depotRiskInPercent(calculateDepotRiskInPercent(exchangeSnapshot, investments))
                .totalRiskInPercent(calculateTotalRiskInPercent(exchangeSnapshot, individualRisk, investments))
                .build();
    }

    public RiskResult calculatePositionRisk(QueryResult queryResult, IndividualRisk individualRisk, List<Investment> investments) {
        return RiskResult.builder()
                .id(individualRisk.getId())
                .totalCapital(individualRisk.getTotalCapital())
                .name(individualRisk.getName())
                .individualPositionRiskInPercent(individualRisk.getIndividualPositionRiskInPercent())
                .individualPositionRisk(individualRisk.calculateIndividualPositionRisk())
                .investments(calculatePositionRisk2(queryResult, investments))
                .totalInvestment(calculateTotalInvestment(investments))
                .totalRevenue(calculateTotalNotionalRevenue(queryResult, investments))
                .depotRisk(calculateDepotRisk(queryResult, investments))
                .depotRiskInPercent(calculateDepotRiskInPercent(queryResult, investments))
                .totalRiskInPercent(calculateTotalRiskInPercent(queryResult, individualRisk, investments))
                .build();
    }

    private List<InvestmentResult> calculatePositionRisk2(ExchangeSnapshot exchangeSnapshot, List<Investment> investments) {
        List<InvestmentResult> investmentResults = new ArrayList<>(investments.size());
        investments.forEach(investment -> investmentResults.add(calculateResult(exchangeSnapshot, investment)));

        investmentResults.sort(comparing(InvestmentResult::getPositionRisk));

        return investmentResults;
    }

    private List<InvestmentResult> calculatePositionRisk2(QueryResult queryResult, List<Investment> investments) {
        List<InvestmentResult> investmentResults = new ArrayList<>(investments.size());
        investments.forEach(investment -> investmentResults.add(calculateResult(queryResult, investment)));

        investmentResults.sort(comparing(InvestmentResult::getPositionRisk));

        return investmentResults;
    }

    private InvestmentResult calculateResult(ExchangeSnapshot exchangeSnapshot, Investment investment) {
        return InvestmentResult.builder()
                .id(investment.getId())
                .wkn(investment.getWkn())
                .name(investment.getName())
                .quantity(investment.getQuantity())
                .purchasePrice(investment.getPurchasePrice())
                .notionalSalesPrice(maxNotionalPrice(investment.getStopPrice(), calculateMaxNotionalPrice(exchangeSnapshot, investment)))
                .transactionCosts(investment.getTransactionCosts())
                .investment(investment.getInvestmentCapital())
                .notionalRevenue(calculateNotionalRevenue(exchangeSnapshot, investment))
                .positionRisk(calculatePositionRisk(exchangeSnapshot, investment))
                .build();
    }

    private InvestmentResult calculateResult(QueryResult queryResult, Investment investment) {
        return InvestmentResult.builder()
                .id(investment.getId())
                .wkn(investment.getWkn())
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
