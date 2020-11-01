package com.example.demo.boundary;

import com.example.demo.control.managment.ManagementController;
import com.example.demo.control.managment.MoneyManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MoneyManagementBoundary {

    @Autowired
    private ManagementController controller;

    @GetMapping("/management")
    public MoneyManagement moneyManagement() {
        return controller.getMoneyManagement();
    }
}