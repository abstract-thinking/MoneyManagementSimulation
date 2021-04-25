package com.example.risk.boundary.api;

import lombok.Value;

import java.util.List;

@Value
public class SalesRecommendations {

    ExchangeResult exchangeResult;
    List<SaleRecommendation> saleRecommendations;
}
