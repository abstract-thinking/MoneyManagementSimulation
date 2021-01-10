package com.example.risk.boundary;

import com.example.risk.boundary.api.PurchaseRecommendation;
import com.example.risk.boundary.api.RiskResult;
import com.example.risk.boundary.api.SaleRecommendation;
import com.example.risk.control.RiskManagementFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
public class RiskManagementModelController {

    private final RiskManagementFacade riskManagementFacade;

    @GetMapping(path = "/risk/1")
    public String showRiskManagement1(Model model) {
        log.info("Show risk management 1 invoked");

        riskManagementFacade.useFirst();

        RiskResult riskResult = riskManagementFacade.doRiskManagement();
        List<SaleRecommendation> saleRecommendations = riskManagementFacade.doSellRecommendations();
        List<PurchaseRecommendation> purchaseRecommendations = riskManagementFacade.doPurchaseRecommendations();

        model.addAttribute("riskResult", riskResult);
        model.addAttribute("sellRecommendations", saleRecommendations);
        model.addAttribute("buyRecommendations", purchaseRecommendations);

        log.info("Show risk management 1 done");

        return "risk";
    }

    @GetMapping(path = "/risk/2")
    public String showRiskManagement2(Model model) {
        log.info("Show risk management 2 invoked");

        riskManagementFacade.useSecond();

        RiskResult riskResult = riskManagementFacade.doRiskManagement();
        List<SaleRecommendation> saleRecommendations = riskManagementFacade.doSellRecommendations();
        List<PurchaseRecommendation> purchaseRecommendations = riskManagementFacade.doPurchaseRecommendations();

        model.addAttribute("riskResult", riskResult);
        model.addAttribute("sellRecommendations", saleRecommendations);
        model.addAttribute("buyRecommendations", purchaseRecommendations);

        log.info("Show risk management 2 done");

        return "risk";
    }

}
