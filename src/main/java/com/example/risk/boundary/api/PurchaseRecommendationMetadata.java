package com.example.risk.boundary.api;

import lombok.Value;

import java.util.List;

@Value
public class PurchaseRecommendationMetadata {

    String exchange;
    double exchangeRsl;

    List<PurchaseRecommendation> purchaseRecommendations;
}
