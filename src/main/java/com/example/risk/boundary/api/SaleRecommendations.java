package com.example.risk.boundary.api;

import lombok.Value;

import java.util.List;

@Value
public class SaleRecommendations {

    ExchangeResult exchangeResult;
    List<SaleRecommendation> saleRecommendations;
}
