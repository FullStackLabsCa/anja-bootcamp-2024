package io.reactivestax.producer.repository;

import io.reactivestax.producer.type.entity.Position;

public interface PositionsRepository {
    void upsertPosition(Position position);
}
