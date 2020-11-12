package com.example.demo.boundary;

import com.example.demo.control.invest.DecisionRow;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@JsonPropertyOrder({"wkn", "name", "price", "notionalSalesPrice", "rsl", "vola30Day"})
public class BuyRecommendation extends RepresentationModel<BuyRecommendation> {

    private final DecisionRow row;
    private final BigDecimal notionalSalesPrice;

    public BuyRecommendation(DecisionRow row, BigDecimal notionalSalesPrice) {
        this.row = row;
        this.notionalSalesPrice = notionalSalesPrice;
    }

    public String getWkn() {
        return row.getWkn();
    }

    public String getName() {
        return row.getName();
    }

    public BigDecimal getNotionalSalesPrice() {
        return notionalSalesPrice;
    }

    public BigDecimal getPrice() {
        return row.getPrice();
    }

    public double getRsl() {
        return row.getRsl();
    }

    public double getVola30Day() {
        return row.getVola30Day();
    }
}
