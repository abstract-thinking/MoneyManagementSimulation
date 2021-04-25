package com.example.risk.boundary.api;

import lombok.Value;

import java.util.List;

@Value
public class PurchaseRecommendations {

    ExchangeResult exchangeResult;

    List<PurchaseRecommendation> purchaseRecommendations;
}
