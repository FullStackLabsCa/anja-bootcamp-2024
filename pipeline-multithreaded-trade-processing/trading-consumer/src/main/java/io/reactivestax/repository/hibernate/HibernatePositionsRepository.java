package io.reactivestax.repository.hibernate;

import org.hibernate.Session;

import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.hibernate.entity.Position;
import io.reactivestax.repository.hibernate.entity.PositionCompositeKey;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import jakarta.persistence.NoResultException;

public class HibernatePositionsRepository implements PositionsRepository {
    private static HibernatePositionsRepository instance;

    private HibernatePositionsRepository() {
    }

    public static synchronized HibernatePositionsRepository getInstance() {
        if (instance == null) {
            instance = new HibernatePositionsRepository();
        }
        return instance;
    }

    @Override
    public void upsertPosition(io.reactivestax.type.dto.PositionDTO position) {
        Position positionEntity = getPositionEntity(position);
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        try {
            Position position1 = session
                    .createQuery("from Position p where p.positionCompositeKey.accountNumber = :accountNumber and p" +
                            ".positionCompositeKey.securityCusip = " +
                            ":securityCusip", Position.class)
                    .setParameter("accountNumber", positionEntity.getPositionCompositeKey().getAccountNumber())
                    .setParameter("securityCusip", positionEntity.getPositionCompositeKey().getSecurityCusip())
                    .getSingleResult();
            position1.setHolding(position1.getHolding() + position.getHolding());
        } catch (NoResultException e) {
            session.persist(positionEntity);
        }
    }

    private Position getPositionEntity(io.reactivestax.type.dto.PositionDTO position) {
        PositionCompositeKey positionCompositeKey = new PositionCompositeKey();
        positionCompositeKey.setAccountNumber(position.getAccountNumber());
        positionCompositeKey.setSecurityCusip(position.getSecurityCusip());
        Position positionEntity = new Position();
        positionEntity.setPositionCompositeKey(positionCompositeKey);
        positionEntity.setHolding(position.getHolding());

        return positionEntity;
    }
}
