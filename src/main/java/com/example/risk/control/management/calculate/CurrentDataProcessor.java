package com.example.risk.control.management.calculate;

import com.example.risk.boundary.api.CurrentData;
import com.example.risk.boundary.api.CurrentDataResult;
import com.example.risk.converter.ExchangeSnapshot;
import com.example.risk.data.Investment;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparingDouble;

@AllArgsConstructor
public class CurrentDataProcessor {

    private final ExchangeSnapshot exchangeSnapshot;

    public CurrentDataResult process(List<Investment> investments) {
        final List<CurrentData> currentData = new ArrayList<>();

        investments.forEach(investment -> exchangeSnapshot.getData().forEach(data -> {
                    if (investment.getWkn().equalsIgnoreCase(data.getWkn())) {
                        currentData.add(createCurrentData(investment, data));
                    }
                })
        );

        currentData.sort(comparingDouble(CurrentData::getRsl).reversed());

        return new CurrentDataResult(exchangeSnapshot.getName(), exchangeSnapshot.getRsl(), currentData);
    }

    private CurrentData createCurrentData(Investment investment, ExchangeSnapshot.ExchangeData data) {
        return CurrentData.builder()
                .wkn(data.getWkn())
                .name(data.getName())
                .price(data.getPrice())
                .stopPrice(investment.getStopPrice())
                .rsl(data.getRsl())
                .build();
    }
}
