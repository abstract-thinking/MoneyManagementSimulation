package com.example.demo.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final InvestmentRepository investmentRepository;
    private final MoneyManagementRepository  moneyManagementRepository;

    @Autowired
    public DatabaseLoader(InvestmentRepository investmentRepository, MoneyManagementRepository  moneyManagementRepository) {
        this.investmentRepository = investmentRepository;
        this.moneyManagementRepository = moneyManagementRepository;
    }

    @Override
    public void run(String... strings) throws Exception {
        this.investmentRepository.save(Investment.builder()
                .name("Fiserv Inc.")
                .quantity(210)
                .purchasePrice(BigDecimal.valueOf(59.90))
                .notionalSalesPrice(BigDecimal.valueOf(89.00))
                .purchaseCost(BigDecimal.valueOf(30.00))
                .build());

        this.investmentRepository.save(Investment.builder()
                .name("PayPal Holdings Inc.")
                .quantity(13)
                .purchasePrice(BigDecimal.valueOf(166.38))
                .notionalSalesPrice(BigDecimal.valueOf(145.67))
                .purchaseCost(BigDecimal.valueOf(30.00))
                .build());

        this.moneyManagementRepository.save(MoneyManagementValues.builder()
                .totalCapital(BigDecimal.valueOf(30000))
                .individualPositionRiskInPercent(2)
                .build());
    }
}
