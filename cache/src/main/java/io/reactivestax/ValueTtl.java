package io.reactivestax;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ValueTtl<V> {
    private V value;
    @Builder.Default
    private long ttl = 60000;
    @Builder.Default
    @Setter
    private LocalDateTime lastAccessTime = LocalDateTime.now();
}
