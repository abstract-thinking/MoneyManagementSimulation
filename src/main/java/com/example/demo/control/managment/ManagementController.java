package com.example.demo.control.managment;


import com.example.demo.data.MoneyManagement;
import com.example.demo.data.MoneyManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManagementController {

    private final MoneyManagementRepository moneyManagementRepository;

    @Autowired
    public ManagementController(MoneyManagementRepository moneyManagementRepository) {
        this.moneyManagementRepository = moneyManagementRepository;
    }

    public MoneyManagement getMoneyManagement() {
        return moneyManagementRepository.findAll().iterator().next();
    }
}

