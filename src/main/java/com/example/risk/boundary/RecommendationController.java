package com.example.risk.boundary;


import com.example.risk.boundary.api.BuyRecommendation;
import com.example.risk.boundary.api.SellRecommendation;
import com.example.risk.control.RiskManagementFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequestMapping("/api/recommendations")
@RestController
@AllArgsConstructor
public class RecommendationController implements RepresentationModelProcessor<RepositoryLinksResource> {

    private final RiskManagementFacade riskManagementFacade;

    @GetMapping(path = "/sell", produces = APPLICATION_JSON_VALUE)
    public CollectionModel<SellRecommendation> sellRecommendations() {
        List<SellRecommendation> recommendations = riskManagementFacade.doSellRecommendations();

        Link link = linkTo(methodOn(RecommendationController.class).sellRecommendations()).withSelfRel();
        return CollectionModel.of(recommendations, link);
    }

    @GetMapping(path = "/buy", produces = APPLICATION_JSON_VALUE)
    public CollectionModel<BuyRecommendation> buyRecommendations() {
        List<BuyRecommendation> recommendations = riskManagementFacade.doBuyRecommendations();

        Link link = linkTo(methodOn(RecommendationController.class).buyRecommendations()).withSelfRel();
        return CollectionModel.of(recommendations, link);
    }

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(linkTo(RecommendationController.class).withRel("recommendations"));
        resource.add(linkTo(methodOn(RecommendationController.class).sellRecommendations()).withRel("sell"));
        resource.add(linkTo(methodOn(RecommendationController.class).buyRecommendations()).withRel("buy"));

        return resource;
    }
}

