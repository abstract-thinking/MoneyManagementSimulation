package com.example.risk.boundary.api;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class QueryResult {

    final Exchange exchange;

    final List<CompanyResult> companyResults = new ArrayList<>();

    public QueryResult(Exchange exchange) {
        this.exchange = exchange;
    }

    public void add(CompanyResult companyResult) {
        companyResults.add(companyResult);
    }

    public CompanyResult getCurrent() {
        // TODO: This should be the last one (0 or size() - 1)
        return companyResults.get(companyResults.size() - 1);
    }

    @Data
    @Builder
    public static class CompanyResult {
        String symbol;
        String name;
        LocalDate date;
        BigDecimal weeklyPrice;
        double rsl;
        BigDecimal stopPrice;
    }
}
