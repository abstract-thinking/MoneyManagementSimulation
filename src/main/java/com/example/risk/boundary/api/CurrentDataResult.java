package com.example.risk.boundary.api;

import lombok.Value;

import java.util.List;

@Value
public class CurrentDataResult {

    ExchangeResult2 exchangeResult;

    List<CurrentData> currentData;
}
