package com.example.mm.control.invest;

import com.example.mm.boundary.BuyRecommendation;
import com.example.mm.boundary.SellRecommendation;
import com.example.mm.control.management.RiskManagement;
import com.example.mm.converter.DecisionRowConverter;
import com.example.mm.data.Investment;
import com.example.mm.data.InvestmentRepository;
import com.example.mm.data.MoneyManagement;
import com.example.mm.data.MoneyManagementRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.mm.control.management.PriceCalculator.calculateNotionalSalesPrice;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

@Slf4j
@AllArgsConstructor
@Component
public class InvestmentRecommender {

    private static final String EXCHANGE_NAME = "NASDAQ 100";

    private final MoneyManagementRepository moneyManagementRepository;

    private final InvestmentRepository investmentRepository;

    private final DecisionRowConverter converter;

    public List<SellRecommendation> getSellRecommendations() {
        MoneyManagement moneyManagement = moneyManagementRepository.findAll().iterator().next();
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(moneyManagement.getId());
        List<ExchangeResult> results = converter.fetchTable();

        RiskManagement riskManagement = new RiskManagement(moneyManagement.getTotalCapital(),
                moneyManagement.getIndividualPositionRiskInPercent());
        riskManagement.setInvestments(investments);

        final double exchangeRsl = findExchangeRsl(results);
        List<SellRecommendation> sellRecommendations = new ArrayList<>();
        for (Investment investment : investments) {
            results.stream()
                    .filter(result -> result.getWkn().equalsIgnoreCase(investment.getWkn()))
                    .map(result -> createSellRecommendation(result, exchangeRsl))
                    .filter(sellRecommendation -> investment.shouldSell(riskManagement.calculatePositionRisk(), sellRecommendation))
                    .findFirst()
                    .ifPresent(sellRecommendations::add);
        }

        return sellRecommendations;
    }

    public List<SellRecommendation> getSellRecommendations2() {
        Iterator<MoneyManagement> iterator = moneyManagementRepository.findAll().iterator();
        MoneyManagement moneyManagement = iterator.next();
        while (iterator.hasNext()) {
            moneyManagement = iterator.next();
        }
        List<Investment> investments = investmentRepository.findAllByMoneyManagementId(moneyManagement.getId());
        List<ExchangeResult> results = converter.fetchTable();

        RiskManagement riskManagement = new RiskManagement(moneyManagement.getTotalCapital(),
                moneyManagement.getIndividualPositionRiskInPercent());
        riskManagement.setInvestments(investments);

        final double exchangeRsl = findExchangeRsl(results);
        List<SellRecommendation> sellRecommendations = new ArrayList<>();
        for (Investment investment : investments) {
            results.stream()
                    .filter(result -> result.getWkn().equalsIgnoreCase(investment.getWkn()))
                    .map(result -> createSellRecommendation(result, exchangeRsl))
                    .filter(sellRecommendation -> investment.shouldSell(riskManagement.calculatePositionRisk(), sellRecommendation))
                    .findFirst()
                    .ifPresent(sellRecommendations::add);
        }

        return sellRecommendations;
    }


    private double findExchangeRsl(List<ExchangeResult> results) {
        return results.stream()
                .filter(result -> result.getName().equals(EXCHANGE_NAME))
                .findFirst()
                .orElseThrow()
                .getRsl();
    }

    private SellRecommendation createSellRecommendation(ExchangeResult result, double exchangeRsl) {
        return SellRecommendation.builder()
                .wkn(result.getWkn())
                .company(result.getName())
                .companyRsl(result.getRsl())
                .exchange(EXCHANGE_NAME)
                .exchangeRsl(exchangeRsl)
                .price(result.getPrice())
                .build();
    }

    public List<BuyRecommendation> getBuyRecommendations() {
        List<ExchangeResult> results = converter.fetchTable();
        final double exchangeRsl = findExchangeRsl(results);
        return results.stream()
                .filter(result -> result.getRsl() > exchangeRsl)
                .sorted(comparingDouble(ExchangeResult::getRsl).reversed())
                .map(result -> createBuyRecommendation(result, exchangeRsl))
                .collect(toList());
    }

    private BuyRecommendation createBuyRecommendation(ExchangeResult result, double exchangeRsl) {
        return BuyRecommendation.builder()
                .name(result.getName())
                .rsl(result.getRsl())
                .wkn(result.getWkn())
                .vola30Day(result.getVola30Day())
                .price(result.getPrice())
                .exchange(EXCHANGE_NAME)
                .exchangeRsl(exchangeRsl)
                .notionalSalesPrice(calculateNotionalSalesPrice(result.getRsl(), result.getPrice(), exchangeRsl))
                .build();
    }
}
