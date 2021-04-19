package com.example.risk.service;

import com.example.risk.boundary.api.CurrentData;
import com.example.risk.boundary.api.CurrentDataResult;
import com.example.risk.boundary.api.ExchangeResult2;
import com.example.risk.boundary.api.InvestmentResult;
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
import static java.util.Comparator.comparingDouble;

@Service
public class CurrentDataProcessor {

    public CurrentDataResult process(ExchangeSnapshot snapshot, List<Investment> investments) {
        final List<CurrentData> currentData = new ArrayList<>();

        investments.forEach(investment ->
                snapshot.getQuotes().forEach(data -> {
                    if (investment.getWkn().equalsIgnoreCase(data.getWkn())) {
                        currentData.add(createCurrentData(investment, data));
                    }
                })
        );

        currentData.sort(comparingDouble(CurrentData::getRsl).reversed());

        ExchangeResult2 exchangeResult = new ExchangeResult2(snapshot.getExchange().getName(),
                snapshot.getExchange().getRsl());
        return new CurrentDataResult(exchangeResult, currentData);
    }

    private CurrentData createCurrentData(Investment investment, ExchangeSnapshot.Quotes data) {
        return CurrentData.builder()
                .wkn(data.getWkn())
                .name(data.getName())
                .price(data.getPrice())
                .stopPrice(investment.getStopPrice())
                .rsl(data.getRsl())
                .build();
    }

    @Service
    @AllArgsConstructor
    public static class RiskManagementCalculator {

        private BigDecimal calculateDepotRisk(ExchangeSnapshot exchangeSnapshot, List<Investment> investments) {
            return investments.stream()
                    .map(investment -> calculatePositionRisk(exchangeSnapshot, investment))
                    .reduce(ZERO, BigDecimal::add);
        }

        private BigDecimal calculateMaxNotionalPrice(ExchangeSnapshot snapshot, Investment investment) {
            return snapshot.getQuotes().stream()
                    .filter(data -> data.getWkn().equalsIgnoreCase(investment.getWkn()))
                    .findFirst()
                    .map(data -> calculateNotionalSalesPrice(data.getRsl(), data.getPrice(), snapshot.getExchange().getRsl()))
                    .orElseThrow(() -> new RuntimeException("WKN " + investment.getWkn() + " not found"));
        }

        private BigDecimal calculatePositionRisk(ExchangeSnapshot exchangeSnapshot, Investment investment) {
            final BigDecimal profitOrLoss = calculateNotionalRevenue(exchangeSnapshot, investment)
                    .subtract(investment.getInvestmentCapital());

            return profitOrLoss.min(ZERO).negate();
        }

        private double calculateDepotRiskInPercent(ExchangeSnapshot exchangeSnapshot, List<Investment> investments) {
            return calculateDepotRisk(exchangeSnapshot, investments)
                    .divide(calculateTotalNotionalRevenue(exchangeSnapshot, investments), 4, RoundingMode.DOWN)
                    .movePointRight(2)
                    .doubleValue();
        }

        private double calculateTotalRiskInPercent(ExchangeSnapshot exchangeSnapshot, IndividualRisk individualRisk, List<Investment> investments) {
            return calculateDepotRisk(exchangeSnapshot, investments)
                    .divide(calculateCurrentCapital(exchangeSnapshot, individualRisk, investments), 4, RoundingMode.DOWN)
                    .movePointRight(2)
                    .doubleValue();
        }

        private BigDecimal calculateCurrentCapital(ExchangeSnapshot exchangeSnapshot, IndividualRisk individualRisk, List<Investment> investments) {
            return individualRisk.getTotalCapital()
                    .subtract(calculateTotalInvestment(investments))
                    .add(calculateTotalNotionalRevenue(exchangeSnapshot, investments));
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

        private BigDecimal calculateNotionalRevenue(ExchangeSnapshot exchangeSnapshot, Investment investment) {
            return maxNotionalPrice(investment.getStopPrice(), calculateMaxNotionalPrice(exchangeSnapshot, investment))
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

        private List<InvestmentResult> calculatePositionRisk2(
                ExchangeSnapshot exchangeSnapshot, List<Investment> investments) {
            List<InvestmentResult> investmentResults = new ArrayList<>(investments.size());
            investments.forEach(investment -> investmentResults.add(calculateResult(exchangeSnapshot, investment)));

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
    }
}
