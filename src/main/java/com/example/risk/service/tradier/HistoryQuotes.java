package com.example.risk.service.tradier;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HistoryQuotes {

    WeeklyQuotes history;
}
