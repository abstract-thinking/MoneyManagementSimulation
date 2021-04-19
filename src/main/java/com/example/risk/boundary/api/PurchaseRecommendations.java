package com.example.risk.boundary.api;

import lombok.Value;

import java.util.List;

@Value
public class PurchaseRecommendations {

    ExchangeResult2 exchangeResult;

    List<PurchaseRecommendation> purchaseRecommendations;
}
