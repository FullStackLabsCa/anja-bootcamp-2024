package io.reactivestax.repository.hibernate;

import io.reactivestax.entity.Position;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.utility.database.TransactionUtil;
import jakarta.persistence.NoResultException;
import org.hibernate.Session;

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
    public void upsertPosition(Position position) {
        Session session = getSession();
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

    private static Session getSession() {
        TransactionUtil transactionUtil = BeanFactory.getTransactionUtil();
        Session session = (Session)transactionUtil.getConnection();
        return session;
    }
}
