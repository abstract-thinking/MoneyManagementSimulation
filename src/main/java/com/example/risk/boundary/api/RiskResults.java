package com.example.risk.boundary.api;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class RiskResults extends RepresentationModel<RiskResult> {

    List<RiskResult> riskResults;
}
