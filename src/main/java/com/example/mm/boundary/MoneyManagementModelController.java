package com.example.mm.boundary;

import com.example.mm.control.Facade;
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

    private final Facade facade;

    @GetMapping(path = "/mm/1")
    public String showMoneyManagement(Model model) {
        log.info("Show money management invoked");

        facade.useFirst();

        RiskManagementResult riskManagementResult = facade.getMoneyManagement();
        List<SellRecommendation> sellRecommendations = facade.getSellRecommendations();
        List<BuyRecommendation> buyRecommendations = facade.getBuyRecommendations();

        model.addAttribute("moneyManagement", riskManagementResult);
        model.addAttribute("sellRecommendations", sellRecommendations);
        model.addAttribute("buyRecommendations", buyRecommendations);

        log.info("Show money management done");

        return "mm";
    }

    @GetMapping(path = "/mm/2")
    public String showMoneyManagement2(Model model) {
        log.info("Show money management 2 invoked");

        facade.useSecond();

        RiskManagementResult riskManagementResult = facade.getMoneyManagement();
        List<SellRecommendation> sellRecommendations = facade.getSellRecommendations();
        List<BuyRecommendation> buyRecommendations = facade.getBuyRecommendations();

        model.addAttribute("moneyManagement", riskManagementResult);
        model.addAttribute("sellRecommendations", sellRecommendations);
        model.addAttribute("buyRecommendations", buyRecommendations);

        log.info("Show money management 2 done");

        return "mm";
    }

}
