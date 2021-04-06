package com.example.risk.boundary.api;

import lombok.Value;

import java.util.List;

@Value
public class CurrentDataResult {

    String exchange;
    double exchangeRsl;

    List<CurrentData> currentData;
}
