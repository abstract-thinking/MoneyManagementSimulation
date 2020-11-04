package com.example.demo.control.managment;



import com.example.demo.data.Investment;
import com.example.demo.data.InvestmentRepository;
import com.example.demo.data.MoneyManagementRepository;
import com.example.demo.data.MoneyManagementValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ManagementController {

    private final InvestmentRepository investmentRepository;

    private final MoneyManagementRepository moneyManagementRepository;

    @Autowired
    public ManagementController(InvestmentRepository investmentRepository, MoneyManagementRepository moneyManagementRepository) {
        this.investmentRepository = investmentRepository;
        this.moneyManagementRepository = moneyManagementRepository;
    }

    public MoneyManagement getMoneyManagement() {
        List<Investment> investments = new ArrayList<>();
        for (Investment entry : investmentRepository.findAll()) {
            investments.add(entry);
        }

        Iterable<MoneyManagementValues> all = moneyManagementRepository.findAll();
        return new MoneyManagement(all.iterator().next(), new Investments(investments));
    }
}

