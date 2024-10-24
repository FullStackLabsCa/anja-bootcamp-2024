package io.reactivestax.repository;

import io.reactivestax.type.dto.Position;

public interface PositionsRepository {
    void upsertPosition(Position position);
}
