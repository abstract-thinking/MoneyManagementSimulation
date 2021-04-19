package com.example.risk.service.wiki;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode
@ToString
public class Company {
    String name;
    String symbol;
}
