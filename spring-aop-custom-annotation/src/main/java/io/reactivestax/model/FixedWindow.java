package io.reactivestax.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FixedWindow {
    private int remainingRequests;
    private long startTime;
}
