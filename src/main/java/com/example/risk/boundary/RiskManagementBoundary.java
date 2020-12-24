package com.example.risk.boundary;

import com.example.risk.boundary.api.RiskManagementResult;
import com.example.risk.control.Facade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class RiskManagementBoundary {

    private final Facade facade;

    @GetMapping(path = "/risk", produces = "application/json")
    public RiskManagementResult riskManagement() {
        log.info("Money management invoked");

        facade.useFirst();
        return facade.doRiskManagement();
    }

}