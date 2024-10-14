package io.reactivestax.repository;

import io.reactivestax.entity.Position;
import jakarta.persistence.NoResultException;
import org.hibernate.Session;

public class PositionsRepository implements ReadAndUpsertPositions {
    @Override
    public void upsertPosition(Position position, Session session) {
        try {
            Position position1 = session.createQuery("from Position p where p.positionCompositeKey.accountNumber = :accountNumber and p" +
                            ".positionCompositeKey.securityCusip = " +
                            ":securityCusip", Position.class)
                    .setParameter("accountNumber", position.getPositionCompositeKey().getAccountNumber())
                    .setParameter("securityCusip", position.getPositionCompositeKey().getSecurityCusip())
                    .getSingleResult();
            position1.setHolding(position1.getHolding() + position.getHolding());
        } catch (NoResultException e) {
            session.persist(position);
        }
    }
}
