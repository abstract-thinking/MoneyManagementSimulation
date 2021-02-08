package com.example.risk.boundary;

import com.example.risk.boundary.api.InvestmentResult;
import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.RiskResults;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.control.management.RiskManagementFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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

    @GetMapping(path = "/api/risks", produces = APPLICATION_JSON_VALUE)
    public RiskResults riskManagements() {
        log.info("Risk managements invoked");

        RiskResults riskManagementResults = riskManagementFacade.doRiskManagements();
        riskManagementResults.add(linkTo(RiskManagementController.class).slash("api").slash("risk").withSelfRel());
        riskManagementResults.getRiskResults().forEach(this::addSelfLink);

        return riskManagementResults;
    }

    @GetMapping(path = "/api/risks/{riskId}", produces = APPLICATION_JSON_VALUE)
    public RiskResult riskManagement(@PathVariable("riskId") Long riskId) {
        log.info("Risk management invoked");

        RiskResult riskResult;
        try {
           riskResult = riskManagementFacade.doRiskManagement(riskId);
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

                String relativePath = "/api/risks/" + riskResult.getId() + "/recommendations/sales/" + investment.getId();

                Link link = Link.of(relativePath, "sale");
//                Link link = linkTo(RiskManagementControllerContractsMvvBracketIT.class)
//                        .slash("api")
//                        .slash("risks")
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
                .slash("risks")
                .slash(riskResult.getId()).withSelfRel();
    }

    @GetMapping(path = "/api/risks/{riskId}/recommendations/sales", produces = APPLICATION_JSON_VALUE)
    public List<SaleRecommendation> saleRecommendations(@PathVariable("riskId") Long riskId) {
        log.info("Sale recommendations invoked");

        return riskManagementFacade.doSaleRecommendations(riskId);
    }

    @GetMapping(path = "/api/risks/{riskId}/recommendations/sales/{investmentId}", produces = APPLICATION_JSON_VALUE)
    public SaleRecommendation saleRecommendation(
            @PathVariable("riskId") Long riskId,
            @PathVariable("investmentId") Long investmentId) {
        log.info("Sale recommendation invoked");

        return riskManagementFacade.doSaleRecommendation(riskId, investmentId);
    }

    @GetMapping(path = "/api/risks/{riskId}/recommendations/purchases", produces = APPLICATION_JSON_VALUE)
    public List<PurchaseRecommendation> purchaseRecommendations(@PathVariable("riskId") Long riskId) {
        log.info("Purchase recommendations invoked");

        return riskManagementFacade.doPurchaseRecommendations(riskId);
    }

// Seems to be odd and does not work as expected
//    @ExceptionHandler({NoSuchElementException.class})
//    public void handleException() {
//        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//    }

}