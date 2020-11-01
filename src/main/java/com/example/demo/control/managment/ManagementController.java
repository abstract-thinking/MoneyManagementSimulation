package com.example.demo.control.managment;

import com.example.demo.control.managment.MoneyManagement;
import com.example.demo.control.managment.Portfolio;
import com.example.demo.control.managment.PortfolioEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ManagementController {

    public MoneyManagement getMoneyManagement() {
        PortfolioEntry entry1 = PortfolioEntry.builder()
                .name("Fiserv Inc.")
                .quantity(210)
                .purchasePrice(BigDecimal.valueOf(59.90))
                .notionalSalesPrice(BigDecimal.valueOf(89.00))
                .purchaseCost(BigDecimal.valueOf(30.00))
                .build();

        PortfolioEntry entry2 = PortfolioEntry.builder()
                .name("Paypal Inc.")
                .quantity(13)
                .purchasePrice(BigDecimal.valueOf(166.38))
                .notionalSalesPrice(BigDecimal.valueOf(145.67))
                .purchaseCost(BigDecimal.valueOf(30.00))
                .build();

        List<PortfolioEntry> portfolioEntries = new ArrayList<>();
        portfolioEntries.add(entry1);
        portfolioEntries.add(entry2);

        return new MoneyManagement(BigDecimal.valueOf(30000), 2, new Portfolio(portfolioEntries));
    }
}

