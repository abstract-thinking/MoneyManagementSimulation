package com.example.risk.boundary;

import com.example.risk.boundary.api.CalculationResult;
import com.example.risk.boundary.api.CurrentDataResult;
import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.PurchaseRecommendationMetadata;
import com.example.risk.boundary.api.RiskData;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.RiskResults;
import com.example.risk.boundary.api.SalesRecommendationMetadata;
import com.example.risk.control.management.RiskManagementFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin
@Slf4j
@RestController
@AllArgsConstructor
public class RiskManagementController {

    private final RiskManagementFacade riskManagementFacade;

    @GetMapping(path = "/api/riskManagements", produces = APPLICATION_JSON_VALUE)
    public RiskResults riskManagements() {
        log.info("Risk managements invoked");

        RiskResults riskManagementResults = riskManagementFacade.doRiskManagements();
        riskManagementResults.add(linkTo(RiskManagementController.class).slash("api").slash("risk").withSelfRel());
        riskManagementResults.getRiskResults().forEach(this::addSelfLink);

        return riskManagementResults;
    }

    @GetMapping(path = "/api/riskManagements/{riskManagementId}", produces = APPLICATION_JSON_VALUE)
    public RiskResult riskManagement(@PathVariable("riskManagementId") Long riskManagementId) {
        log.info("Risk management invoked");

        RiskResult riskResult;
        try {
            riskResult = riskManagementFacade.doRiskManagement(riskManagementId);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(NOT_FOUND);
        }

        addSelfLink(riskResult);
        addSaleRecommendationLink(riskResult);

        return riskResult;
    }

    private void addSaleRecommendationLink(RiskResult riskResult) {
        for (InvestmentResult investment : riskResult.getInvestments()) {
            if (investment.getHasSellRecommendation()) {

                String relativePath = "/api/riskManagements/" + riskResult.getId() + "/recommendations/sales/" + investment.getId();

                Link link = Link.of(relativePath, "sale");
//                Link link = linkTo(RiskManagementControllerContractsMvvBracketIT.class)
//                        .slash("api")
//                        .slash("riskManagements")
//                        .slash(riskResult.getId())
//                        .slash("sell-recommendation")
//                        .slash(investment.getId())
//                        .withRel("sell-recommendation");

                investment.add(link);
            }
        }
    }

    private void addSelfLink(RiskResult riskResult) {
        riskResult.add(createSelfLink(riskResult));
    }

    private Link createSelfLink(RiskResult riskResult) {
        return linkTo(RiskManagementController.class)
                .slash("api")
                .slash("riskManagements")
                .slash(riskResult.getId()).withSelfRel();
    }

    @GetMapping(path = "/api/riskManagements/{riskManagementId}/recommendations/sales", produces = APPLICATION_JSON_VALUE)
    public SalesRecommendationMetadata saleRecommendations(@PathVariable("riskManagementId") Long riskManagementId) {
        log.info("Sale recommendations invoked");

        return riskManagementFacade.doSaleRecommendations(riskManagementId);
    }

    @GetMapping(path = "/api/riskManagements/{riskManagementId}/recommendations/sales/{investmentId}", produces = APPLICATION_JSON_VALUE)
    public SalesRecommendationMetadata saleRecommendation(
            @PathVariable("riskManagementId") Long riskManagementId,
            @PathVariable("investmentId") Long investmentId) {
        log.info("Sale recommendation invoked");

        return riskManagementFacade.doSaleRecommendation(riskManagementId, investmentId);
    }

    @GetMapping(path = "/api/riskManagements/{riskManagementId}/recommendations/purchases", produces = APPLICATION_JSON_VALUE)
    public PurchaseRecommendationMetadata purchaseRecommendations(@PathVariable Long riskManagementId) {
        log.info("Purchase recommendations invoked");

        return riskManagementFacade.doPurchaseRecommendations(riskManagementId);
    }

    @PostMapping("/api/riskManagements/{riskManagementId}")
    public InvestmentResult create(@PathVariable Long riskManagementId,
                                   @RequestBody InvestmentResult newInvestment) {
        return riskManagementFacade.doCreateInvestment(riskManagementId, newInvestment);
    }

    @DeleteMapping("/api/riskManagements/{riskManagementId}/investments/{investmentId}")
    public void delete(@PathVariable Long investmentId) {
        riskManagementFacade.doDeleteInvestment(investmentId);
    }

    @GetMapping(path = "/api/riskManagements/{riskManagementId}/calc", produces = APPLICATION_JSON_VALUE)
    public CalculationResult positionCalculation(
            @PathVariable Long riskManagementId,
            @RequestParam String wkn) {
        return riskManagementFacade.doPositionCalculation(riskManagementId, wkn);
    }

    @PutMapping("/api/riskManagements/{riskManagementId}")
    public void updateCoreData(@PathVariable Long riskManagementId,
                               @RequestBody RiskData riskData) {
        riskManagementFacade.doUpdateCoreData(riskManagementId, riskData);
    }

    @GetMapping(path = "/api/riskManagements/{riskManagementId}/current", produces = APPLICATION_JSON_VALUE)
    public CurrentDataResult showCurrent(@PathVariable Long riskManagementId) {
        return riskManagementFacade.doCurrent(riskManagementId);
    }


}