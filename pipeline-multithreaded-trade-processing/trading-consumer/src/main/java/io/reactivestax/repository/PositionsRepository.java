package io.reactivestax.consumer.repository;

import io.reactivestax.consumer.type.entity.Position;

public interface PositionsRepository {
    void upsertPosition(Position position);
}
