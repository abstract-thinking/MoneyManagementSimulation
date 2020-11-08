package com.example.demo.boundary;

import com.example.demo.control.managment.ManagementController;
import com.example.demo.data.MoneyManagement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class MoneyManagementBoundary {

    private final ManagementController controller;

    @GetMapping(path = "/management", produces = "application/json")
    public MoneyManagement moneyManagement() {
        log.info("Money management invoked");

        return controller.getMoneyManagement();
    }
}