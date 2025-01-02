package io.reactivestax.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Trade {
    private String tradeNo;
    private String cusip;

    @Override
    public String toString() {
        return "Trade{" +
                "tradeNo='" + tradeNo + '\'' +
                ", cusip='" + cusip + '\'' +
                '}';
    }
}
