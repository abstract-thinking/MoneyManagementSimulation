package com.example.risk.boundary;

import com.example.risk.boundary.api.RiskManagementResult;
import com.example.risk.boundary.api.RiskManagementResultList;
import com.example.risk.control.Facade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@AllArgsConstructor
public class RiskManagementBoundary {

    private final Facade facade;

    @GetMapping(path = "/rest/risk", produces = "application/json")
    public RiskManagementResultList riskManagements() {
        log.info("Risk managements invoked");

        RiskManagementResultList riskManagementResults = facade.doRiskManagements();
        riskManagementResults.add(linkTo(RiskManagementBoundary.class).slash("rest").slash("risk").withSelfRel());
        riskManagementResults.getRiskManagementResults().forEach(this::addSelfLink);

        return riskManagementResults;
    }

    @GetMapping(path = "/rest/risk/{id}", produces = "application/json")
    public RiskManagementResult riskManagement(@PathVariable("id") Long id) {
        log.info("Risk management invoked");

        RiskManagementResult riskManagementResult = facade.doRiskManagement(id);
        addSelfLink(riskManagementResult);

        return riskManagementResult;
    }

    private void addSelfLink(RiskManagementResult riskManagementResult) {
        riskManagementResult.add(createSelfLink(riskManagementResult));
    }

    private Link createSelfLink(RiskManagementResult riskManagementResult) {
        return linkTo(RiskManagementBoundary.class)
                .slash("rest")
                .slash("risk")
                .slash(riskManagementResult.getId()).withSelfRel();
    }

    @ExceptionHandler({NoSuchElementException.class})
    public void handleException() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}