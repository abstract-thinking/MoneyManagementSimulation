package com.example.risk.control.management;

import com.example.risk.boundary.api.CalculationResult;
import com.example.risk.boundary.api.CurrentDataResult;
import com.example.risk.boundary.api.Exchange;
import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.PurchaseRecommendations;
import com.example.risk.boundary.api.QueryResult;
import com.example.risk.boundary.api.RiskData;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.RiskResults;
import com.example.risk.boundary.api.SaleRecommendations;
import com.example.risk.data.IndividualRisk;
import com.example.risk.data.Investment;
import com.example.risk.data.InvestmentRepository;
import com.example.risk.data.RiskManagementRepository;
import com.example.risk.service.CurrentDataProcessor;
import com.example.risk.service.InvestmentRecommender;
import com.example.risk.service.PositionCalculator;
import com.example.risk.service.PriceCalculator;
import com.example.risk.service.RelativeStrengthCalculator;
import com.example.risk.service.RiskManagementCalculator;
import com.example.risk.service.tradier.Quote;
import com.example.risk.service.tradier.TradierService;
import com.example.risk.service.wiki.Company;
import com.example.risk.service.wiki.WikiService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.singletonList;

@AllArgsConstructor
@Component
public class RiskManagementFacade {

    private final RiskManagementRepository riskManagementRepository;

    private final InvestmentRepository investmentRepository;

    private final RiskManagementCalculator riskManagementCalculator;

    private final CurrentDataProcessor currentDataProcessor;

    private final WikiService wikiService;

    private final TradierService tradierService;

    private final RelativeStrengthCalculator relativeStrengthCalculator;

    private final PriceCalculator priceCalculator;

    private final InvestmentRecommender investmentRecommender;

    private final PositionCalculator positionCalculator;

    public RiskResults doRiskManagements() {
        final List<RiskResult> riskResults = new ArrayList<>();

        riskManagementRepository.findAll().forEach(risk -> {
                    final List<Investment> investments = investmentRepository.findAllByRiskManagementId(risk.getId());
                    riskResults.add(riskManagementCalculator.calculatePositionRisk(fetchExchangeData(), risk, investments));
                }
        );

        return new RiskResults(riskResults);
    }

    public RiskResult doRiskManagement(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());

        final RiskResult riskResult = riskManagementCalculator.calculatePositionRisk(fetchExchangeData(), individualRisk, investments);

        investmentRecommender.findSaleRecommendations(fetchExchangeData(), investments)
                .getSaleRecommendations().forEach(sellRecommendation ->
                riskResult.getInvestments().stream()
                        .filter(investment -> sellRecommendation.getSymbol().equalsIgnoreCase(investment.getSymbol()))
                        .findFirst()
                        .ifPresent(investment -> investment.setSaleRecommendation(sellRecommendation))
        );

        return riskResult;
    }

    public SaleRecommendations doSaleRecommendations(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());

        return investmentRecommender.findSaleRecommendations(fetchExchangeData(), investments);
    }

    public SaleRecommendations doSaleRecommendation(Long riskManagementId, Long investmentId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());
        final Investment investment = investments.stream()
                .filter(i -> i.getId().equals(investmentId))
                .findFirst()
                .orElseThrow();

        return investmentRecommender.findSaleRecommendations(fetchExchangeData(), singletonList(investment));
    }

    public PurchaseRecommendations doPurchaseRecommendations(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        final PurchaseRecommendations purchaseRecommendations =
                investmentRecommender.findPurchaseRecommendations(fetchExchangeData(), individualRisk);

        removeIfAlreadyInvestedSymbol(individualRisk, purchaseRecommendations);

        return purchaseRecommendations;
    }

    public PurchaseRecommendation doPurchaseRecommendation(Long riskManagementId, String symbol) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        return investmentRecommender.findPurchaseRecommendations(fetchExchangeData(), individualRisk)
                .getPurchaseRecommendations().stream()
                .filter(purchaseRecommendation -> purchaseRecommendation.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .orElseThrow();
    }

    private QueryResult fetchExchangeData() {
        try {
            return doIt(new Exchange("NASDAQ 100", "NASDAQ-100", BigDecimal.valueOf(60)));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void removeIfAlreadyInvestedSymbol(IndividualRisk individualRisk, PurchaseRecommendations purchaseRecommendations) {
        investmentRepository.findAllByRiskManagementId(individualRisk.getId()).forEach(investment ->
                purchaseRecommendations.getPurchaseRecommendations().removeIf(recommendation ->
                        recommendation.getSymbol().equalsIgnoreCase(investment.getSymbol())));
    }

    public InvestmentResult doCreateInvestment(Long riskManagementId, InvestmentResult newInvestment) {
        final Investment investment = investmentRepository.save(toInvestment(riskManagementId, newInvestment));

        return InvestmentResult.builder()
                .id(investment.getId())
                .quantity(investment.getQuantity())
                .purchasePrice(investment.getPurchasePrice())
                .notionalSalesPrice(investment.getStopPrice())
                .transactionCosts(investment.getTransactionCosts())
                .build();
    }

    private Investment toInvestment(Long riskManagementId, InvestmentResult newInvestment) {
        return Investment.builder()
                .name(newInvestment.getName())
                .quantity(newInvestment.getQuantity())
                .purchasePrice(newInvestment.getPurchasePrice())
                .stopPrice(newInvestment.getNotionalSalesPrice())
                .transactionCosts(newInvestment.getTransactionCosts())
                .riskManagementId(riskManagementId)
                .build();
    }

    public void doDeleteInvestment(Long investmentId) {
        investmentRepository.deleteById(investmentId);
    }

    public CalculationResult doPositionCalculation(long riskManagementId, String symbol) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        try {
            List<Quote> quotes = tradierService.fetchWeeklyQuotes(symbol).get();
            final BigDecimal currentStopPrice = priceCalculator.calculateStopPrice(quotes);
            return positionCalculator.calculate(fetchExchangeData(), symbol, individualRisk, currentStopPrice);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void doUpdateCoreData(Long riskManagementId, RiskData riskData) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();

        if (riskData.getTotalCapital() != null) {
            individualRisk.setTotalCapital(riskData.getTotalCapital());
        }

        if (!Double.isNaN(riskData.getIndividualPositionRiskInPercent())) {
            individualRisk.setIndividualPositionRiskInPercent(riskData.getIndividualPositionRiskInPercent());
        }

        riskManagementRepository.save(individualRisk);
    }

    public CurrentDataResult doCurrent(Long riskManagementId) {
        final IndividualRisk individualRisk = riskManagementRepository.findById(riskManagementId).orElseThrow();
        final List<Investment> investments = investmentRepository.findAllByRiskManagementId(individualRisk.getId());

        return currentDataProcessor.process(fetchExchangeData(), investments);
    }

    private QueryResult doIt(Exchange exchange) throws ExecutionException, InterruptedException {
        List<Company> companies = wikiService.fetchCompanies(exchange.getSymbol());

        Map<Company, CompletableFuture<List<Quote>>> futures = new HashMap<>();
        for (Company company : companies) {
            futures.put(company, tradierService.fetchWeeklyQuotes(company.getSymbol()));
        }

        QueryResult result = new QueryResult(exchange);
        for (Map.Entry<Company, CompletableFuture<List<Quote>>> future : futures.entrySet()) {
            final List<Quote> quotes = future.getValue().get();
            final double rsl = relativeStrengthCalculator.calculateRelativeStrengthLevy(quotes);
            final BigDecimal currentStopPrice = priceCalculator.calculateStopPrice(quotes);

            final Quote lastQuote = quotes.isEmpty() ? null : quotes.get(quotes.size() - 1);
            result.add(createCompanyResult(future.getKey(), rsl, currentStopPrice, lastQuote));
        }

        result.getExchange().setRsl(relativeStrengthCalculator.calculateExchangeRsl(result.getCompanyResults()));

        return result;
    }

    private QueryResult.CompanyResult createCompanyResult(Company company, double rsl,
                                                          BigDecimal currentStopPrice, Quote quote) {
        return QueryResult.CompanyResult.builder()
                .symbol(company.getSymbol())
                .name(company.getName())
                .rsl(rsl)
                .currentStopPrice(currentStopPrice)
                .date(quote == null ? LocalDate.MIN : quote.getDate())
                .weeklyPrice(quote == null ? BigDecimal.ZERO : quote.getClose())
                .build();
    }
}
