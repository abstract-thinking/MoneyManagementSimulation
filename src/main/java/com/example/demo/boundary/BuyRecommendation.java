package com.example.demo.boundary;

import com.example.demo.control.invest.DecisionRow;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

public class BuyRecommendation extends RepresentationModel<BuyRecommendation> {

    private final DecisionRow value;
    private final BigDecimal notionalSalesPrice;

    public BuyRecommendation(DecisionRow value, BigDecimal notionalSalesPrice) {
        this.value = value;
        this.notionalSalesPrice = notionalSalesPrice;
    }

    public String getWkn() {
        return value.getWkn();
    }

    public String getName() {
        return value.getName();
    }

    public BigDecimal getNotionalSalesPrice() {
        return notionalSalesPrice;
    }

    public BigDecimal getPrice() {
        return value.getPrice();
    }

    public double getRsl() {
        return value.getRsl();
    }

    public double getVola30Day() {
        return value.getVola30Day();
    }
}
