package io.reactivestax.repository;

import io.reactivestax.type.dto.PositionDTO;

public interface PositionsRepository {
    void upsertPosition(PositionDTO position);
}
