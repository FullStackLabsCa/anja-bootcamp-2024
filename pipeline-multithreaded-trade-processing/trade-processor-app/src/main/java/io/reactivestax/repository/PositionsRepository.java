package io.reactivestax.repository;

import io.reactivestax.entity.Position;
import org.hibernate.Session;

public interface PositionsRepository {
    void upsertPosition(Position position, Session session);
}
