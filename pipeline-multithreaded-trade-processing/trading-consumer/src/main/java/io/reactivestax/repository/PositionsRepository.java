package io.reactivestax.repository;

import io.reactivestax.repository.hibernate.entity.Position;
import io.reactivestax.type.dto.PositionDTO;

public interface PositionsRepository {
    void upsertPosition(PositionDTO position);

    Position findPositionByPositionDetails(PositionDTO positionDTO);

}
