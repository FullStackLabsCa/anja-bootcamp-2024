package io.reactivestax.repository;

import io.reactivestax.entity.Position;

public interface PositionsRepository {
    void upsertPosition(Position position);
}
