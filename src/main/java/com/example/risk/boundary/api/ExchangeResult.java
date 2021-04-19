package com.example.risk.boundary.api;

import com.example.risk.service.wiki.Company;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExchangeResult {

    final String name;
    double rsl;

    List<CompanyResult> companyResults = new ArrayList<>();

    public ExchangeResult(String name) {
        this.name = name;
    }

    public void add(CompanyResult companyResult) {
        companyResults.add(companyResult);
    }

    @Data
    @Builder
    @ToString
    public static class CompanyResult {
        Company company;
        LocalDate date;
        BigDecimal weeklyPrice;
        double rsl;
        BigDecimal stopPrice;
    }

}
