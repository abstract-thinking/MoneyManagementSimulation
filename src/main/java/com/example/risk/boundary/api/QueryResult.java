package com.example.risk.boundary.api;

import com.example.risk.control.management.Exchange;
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
