package com.example.risk.boundary.api;

import lombok.Value;

import java.util.List;

@Value
public class SalesRecommendationMetadata {

    String exchange;
    double exchangeRsl;

    List<SaleRecommendation> saleRecommendations;
}
