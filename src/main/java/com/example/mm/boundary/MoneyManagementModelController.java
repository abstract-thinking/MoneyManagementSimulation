package com.example.mm.boundary;

import com.example.mm.control.invest.InvestmentRecommender;
import com.example.mm.control.management.ManagementController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
public class MoneyManagementModelController {

    private final ManagementController moneyManagementController;

    private final InvestmentRecommender investmentRecommender;

    @GetMapping(path = "/mm/1")
    public String showMoneyManagement(Model model) {
        log.info("Show money management invoked");

        // TODO: Where is the common intelligence?
        RiskManagementApi riskManagementApi = moneyManagementController.getMoneyManagement();
        List<SellRecommendation> sellRecommendations = investmentRecommender.getSellRecommendations();
        List<BuyRecommendation> buyRecommendations = investmentRecommender.getBuyRecommendations();

        model.addAttribute("moneyManagement", riskManagementApi);
        model.addAttribute("sellRecommendations", sellRecommendations);
        model.addAttribute("buyRecommendations", buyRecommendations);

        log.info("Show money management done");

        return "mm";
    }

    @GetMapping(path = "/mm/2")
    public String showMoneyManagement2(Model model) {
        log.info("Show money management 2 invoked");

        // TODO: Where is the logic?
        RiskManagementApi moneyManagement = moneyManagementController.getMoneyManagement2();
        List<SellRecommendation> sellRecommendations = investmentRecommender.getSellRecommendations2();
        List<BuyRecommendation> buyRecommendations = investmentRecommender.getBuyRecommendations();

        model.addAttribute("moneyManagement", moneyManagement);
        model.addAttribute("sellRecommendations", sellRecommendations);
        model.addAttribute("buyRecommendations", buyRecommendations);

        log.info("Show money management 2 done");

        return "mm";
    }

}
