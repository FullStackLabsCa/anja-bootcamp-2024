package io.reactivestax.repository.hibernate;

import io.reactivestax.repository.hibernate.entity.Position;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HibernatePositionsRepositoryTest {
    private final HibernatePositionsRepository hibernatePositionsRepository =
            HibernatePositionsRepository.getInstance();
    private final HibernateTransactionUtil hibernateTransactionUtil = HibernateTransactionUtil.getInstance();

    private final Supplier<io.reactivestax.type.dto.Position> positionSupplier =
            () -> io.reactivestax.type.dto.Position.builder()
                    .accountNumber("TDB_CUST_5214938")
                    .securityCusip("TSLA")
                    .holding(10L)
                    .build();

    @BeforeEach
    void setUp() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance(
                "applicationHibernateTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationHibernateTest.properties");
    }

    @AfterEach
    void tearDown() {
        hibernateTransactionUtil.startTransaction();
        Session session = hibernateTransactionUtil.getConnection();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaDelete<Position> positionCriteriaDelete = criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.Position.class);
        session.createMutationQuery(positionCriteriaDelete).executeUpdate();
        hibernateTransactionUtil.commitTransaction();
    }

    @Test
    void testInsertPosition() {
        io.reactivestax.type.dto.Position positionDto = positionSupplier.get();
        hibernateTransactionUtil.startTransaction();
        hibernatePositionsRepository.upsertPosition(positionDto);
        Position position = fetchPosition(positionDto);
        assertNotNull(position);
        assertEquals(0, position.getVersion());
        assertEquals(10L, position.getHolding());
        hibernateTransactionUtil.rollbackTransaction();
    }

    @Test
    void testUpsertPosition() {
        io.reactivestax.type.dto.Position positionDto = positionSupplier.get();
        hibernateTransactionUtil.startTransaction();
        hibernatePositionsRepository.upsertPosition(positionDto);
        hibernatePositionsRepository.upsertPosition(positionDto);
        Position position = fetchPosition(positionDto);
        assertNotNull(position);
        assertEquals(1, position.getVersion());
        assertEquals(20L, position.getHolding());
        hibernateTransactionUtil.rollbackTransaction();
    }

    private Position fetchPosition(io.reactivestax.type.dto.Position positionDto) {
        Session session = hibernateTransactionUtil.getConnection();
        return session
                .createQuery("from Position p where p.positionCompositeKey.accountNumber = :accountNumber and" +
                        " p.positionCompositeKey.securityCusip = :securityCusip", Position.class)
                .setParameter("accountNumber", positionDto.getAccountNumber())
                .setParameter("securityCusip", positionDto.getSecurityCusip())
                .getSingleResult();
    }
}
