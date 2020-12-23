package com.example.mm.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final InvestmentRepository investmentRepository;
    private final MoneyManagementRepository moneyManagementRepository;

    @Autowired
    public DatabaseLoader(InvestmentRepository investmentRepository, MoneyManagementRepository moneyManagementRepository) {
        this.investmentRepository = investmentRepository;
        this.moneyManagementRepository = moneyManagementRepository;
    }

    @Override
    public void run(String... strings) {
        BigDecimal purchaseCost = BigDecimal.valueOf(35.50);

        createMoneyManagement1(purchaseCost);
        createMoneyManagement2(purchaseCost);
    }

    private void createMoneyManagement2(BigDecimal purchaseCost) {
        MoneyManagement moneyManagement2 = MoneyManagement.builder()
                .totalCapital(BigDecimal.valueOf(100000))
                .individualPositionRiskInPercent(10)
                .build();
        this.moneyManagementRepository.save(moneyManagement2);

        this.investmentRepository.save(Investment.builder()
                .wkn("590375")
                .name("Align Technology Inc.")
                .quantity(5)
                .purchasePrice(BigDecimal.valueOf(426.08))
                .notionalSalesPrice(BigDecimal.valueOf(1000))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement2.getId())
                .build());

        this.investmentRepository.save(Investment.builder()
                .wkn("888210")
                .name("IDEXX Laboratories Inc.")
                .quantity(11)
                .purchasePrice(BigDecimal.valueOf(424.82))
                .notionalSalesPrice(BigDecimal.valueOf(1000))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement2.getId())
                .build());

        this.investmentRepository.save(Investment.builder()
                .wkn("A2JRK6")
                .name("Pinduoduo Inc.")
                .quantity(18)
                .purchasePrice(BigDecimal.valueOf(149.37))
                .notionalSalesPrice(BigDecimal.valueOf(1000))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement2.getId())
                .build());

        this.investmentRepository.save(Investment.builder()
                .wkn("A0MYNP")
                .name("MercadoLibre S.A.")
                .quantity(3)
                .purchasePrice(BigDecimal.valueOf(1214.05))
                .notionalSalesPrice(BigDecimal.valueOf(10000))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement2.getId())
                .build());

        this.investmentRepository.save(Investment.builder()
                .wkn("883121")
                .name("Qualcomm")
                .quantity(37)
                .purchasePrice(BigDecimal.valueOf(123.36))
                .notionalSalesPrice(BigDecimal.valueOf(1000))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement2.getId())
                .build());

        this.investmentRepository.save(Investment.builder()
                .wkn("A1CX3T")
                .name("Tesla Incorporated")
                .quantity(7)
                .purchasePrice(BigDecimal.valueOf(388.04))
                .notionalSalesPrice(BigDecimal.valueOf(1000))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement2.getId())
                .build());

        this.investmentRepository.save(Investment.builder()
                .wkn("A2N9D9")
                .name("Moderna Incorporated")
                .quantity(7)
                .purchasePrice(BigDecimal.valueOf(156.93))
                .notionalSalesPrice(BigDecimal.valueOf(1000))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement2.getId())
                .build());
    }

    private void createMoneyManagement1(BigDecimal purchaseCost) {
        MoneyManagement moneyManagement = MoneyManagement.builder()
                .totalCapital(BigDecimal.valueOf(35500))
                .individualPositionRiskInPercent(1.5)
                .build();
        this.moneyManagementRepository.save(moneyManagement);

        this.investmentRepository.save(Investment.builder()
                .wkn("881793")
                .name("Fiserv Inc.")
                .quantity(210)
                .purchasePrice(BigDecimal.valueOf(73.50))
                .notionalSalesPrice(BigDecimal.valueOf(90.71))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement.getId())
                .build());

        this.investmentRepository.save(Investment.builder()
                .wkn("A14R7U")
                .name("PayPal Holdings Inc.")
                .quantity(13)
                .purchasePrice(BigDecimal.valueOf(196))
                .notionalSalesPrice(BigDecimal.valueOf(163.33))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement.getId())
                .build());

        this.investmentRepository.save(Investment.builder()
                .wkn("590375")
                .name("Align Technology Inc.")
                .quantity(5)
                .purchasePrice(BigDecimal.valueOf(481.54))
                .notionalSalesPrice(BigDecimal.valueOf(401.28))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement.getId())
                .build());

        this.investmentRepository.save(Investment.builder()
                .wkn("883121")
                .name("Qualcomm")
                .quantity(35)
                .purchasePrice(BigDecimal.valueOf(146.70))
                .notionalSalesPrice(BigDecimal.valueOf(128.07))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement.getId())
                .build());

        this.investmentRepository.save(Investment.builder()
                .wkn("869686")
                .name("LAM Research Corporation")
                .quantity(7)
                .purchasePrice(BigDecimal.valueOf(436.49))
                .notionalSalesPrice(BigDecimal.valueOf(375.10))
                .transactionCosts(purchaseCost)
                .moneyManagementId(moneyManagement.getId())
                .build());
    }
}
