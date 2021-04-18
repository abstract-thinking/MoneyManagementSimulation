package com.example.risk.service.tradier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class WeeklyQuotes {

    @JsonProperty("day")
    List<Quote> weekly;
}
