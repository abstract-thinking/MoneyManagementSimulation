package com.example.mm.boundary;

import com.example.mm.control.Facade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class MoneyManagementBoundary {

    private final Facade facade;

    @GetMapping(path = "/management", produces = "application/json")
    public RiskManagementResult moneyManagement() {
        log.info("Money management invoked");

        facade.useFirst();
        return facade.getMoneyManagement();
    }

}