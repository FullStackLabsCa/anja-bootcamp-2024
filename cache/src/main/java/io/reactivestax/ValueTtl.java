package io.reactivestax;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ValueTtl<K, V> {
    private K key;
    private V value;
    @Builder.Default
    private long ttl = 10000;
    @Builder.Default
    @Setter
    private LocalDateTime lastAccessTime = LocalDateTime.now();
}
