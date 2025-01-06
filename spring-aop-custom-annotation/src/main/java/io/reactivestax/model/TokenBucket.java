package io.reactivestax.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenBucket {
    private int remainingRequests;
    private Long lastRemovalTime;
}
