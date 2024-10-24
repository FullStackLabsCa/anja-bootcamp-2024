package io.reactivestax.repository;

import io.reactivestax.type.entity.Position;

public interface PositionsRepository {
    void upsertPosition(Position position);
}
