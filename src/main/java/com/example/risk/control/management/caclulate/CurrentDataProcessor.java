package com.example.risk.control.management.caclulate;

import com.example.risk.boundary.api.CurrentData;
import com.example.risk.boundary.api.CurrentDataResult;
import com.example.risk.converter.ExchangeData;
import com.example.risk.data.Investment;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CurrentDataProcessor {

    private final ExchangeSnapshot exchangeSnapshot;

    public CurrentDataResult process(List<Investment> investments) {
        List<CurrentData> currentData = new ArrayList<>();

        for (Investment investment : investments) {
            for (ExchangeData data : exchangeSnapshot.getExchangeData()) {
                if (investment.getWkn().equalsIgnoreCase(data.getWkn())) {
                    currentData.add(creteCurrentData(investment, data));
                }
            }
        }

        return new CurrentDataResult(exchangeSnapshot.getExchangeName(), exchangeSnapshot.getExchangeRsl(), currentData);
    }

    private CurrentData creteCurrentData(Investment investment, ExchangeData data) {
        return CurrentData.builder()
                .wkn(data.getWkn())
                .name(data.getName())
                .price(data.getPrice())
                .stopPrice(investment.getStopPrice())
                .rsl(data.getRsl())
                .build();
    }

}
