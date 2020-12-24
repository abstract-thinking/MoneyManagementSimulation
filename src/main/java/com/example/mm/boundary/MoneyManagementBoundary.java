package com.example.mm.boundary;

import com.example.mm.control.management.ManagementController;
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
    public RiskManagementResult moneyManagement() {
        log.info("Money management invoked");

        return controller.getMoneyManagement();
    }

}