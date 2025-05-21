package io.reactivestax.multithreading.tradeproducerconsumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trade {
    private Long id;
    private String symbol;
    private int quantity;
    private Double price;
    private Long timestamp;
}
