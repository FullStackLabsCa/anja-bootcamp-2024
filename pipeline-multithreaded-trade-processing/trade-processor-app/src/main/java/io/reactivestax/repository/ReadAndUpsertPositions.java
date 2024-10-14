package io.reactivestax.repository;

import io.reactivestax.entity.Position;
import org.hibernate.Session;

public interface ReadAndUpsertPositions {
    void upsertPosition(Position position, Session session);
}
