package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.entity.FamilyGroup;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FamilyMemberRepositoryTest {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyGroupRepository familyGroupRepository;

    private FamilyGroup familyGroup;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeAll
    void setUp() {
        FamilyGroup familyGroupEntity = FamilyGroup.builder()
                .familyPin(TestData.PASSWORD)
                .status(Status.ACTIVE)
                .credits(0.0)
                .failedLoginAttempts(0)
                .build();
        familyGroup = familyGroupRepository.save(familyGroupEntity);
    }

    @Test
    void testSave() {
        FamilyMember familyMember = getFamilyMember();
        FamilyMember saved = familyMemberRepository.save(familyMember);

        assertThat(saved).isNotNull();
        assertThat(saved.getFamilyMemberId()).isNotNull();
    }

    @Test
    void testFindByFamilyMemberIdAndIsActive() {
        FamilyMember saved = saveFamilyMember();
        Optional<FamilyMember> byFamilyMemberIdAndIsActive = familyMemberRepository.findByFamilyMemberIdAndIsActive(saved.getFamilyMemberId(), true);

        assertThat(byFamilyMemberIdAndIsActive).isPresent();
    }

    @Test
    void testFindByMemberLoginId() {
        FamilyMember saved = saveFamilyMember();
        Optional<FamilyMember> byMemberLoginId = familyMemberRepository.findByMemberLoginId(saved.getMemberLoginId());

        assertThat(byMemberLoginId).isPresent();
    }

    @Test
    void testExistsByFamilyMemberIdAndIsActive() {
        FamilyMember saved = saveFamilyMember();
        boolean exists = familyMemberRepository.existsByFamilyMemberIdAndIsActive(saved.getFamilyMemberId(), saved.isActive());

        assertTrue(exists);
    }

    @Test
    void testExistsByMemberLoginId() {
        FamilyMember familyMember = saveFamilyMember();
        boolean exists = familyMemberRepository.existsByMemberLoginId(familyMember.getMemberLoginId());

        assertTrue(exists);
    }

    @Test
    void testFindByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId() {
        FamilyMember familyMember = saveFamilyMember();
        Optional<FamilyMember> familyMemberOptional = familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (familyMember.getMemberLoginId(), familyMember.isActive(),
                        familyMember.getFamilyGroup().getFamilyGroupId());

        assertThat(familyMemberOptional).isPresent();
    }

    @Test
    void testExistsByMemberLoginIdAndFamilyGroup_FamilyGroupId() {
        FamilyMember familyMember = saveFamilyMember();
        boolean exists = familyMemberRepository.existsByMemberLoginIdAndFamilyGroup_FamilyGroupId(familyMember.getMemberLoginId(),
                familyMember.getFamilyGroup().getFamilyGroupId());

        assertTrue(exists);
    }

    // Using entity manager to clear the persistent context to force fetch from the db
    @Test
    void testUpdateIsActiveByFamilyMemberId() {
        FamilyMember familyMember = saveFamilyMember();
        familyMemberRepository.updateIsActiveByFamilyMemberId(familyMember.getFamilyMemberId(), false);
        entityManager.flush();
        entityManager.clear();

        Optional<FamilyMember> familyMemberOptional = familyMemberRepository.findById(familyMember.getFamilyMemberId());

        assertThat(familyMemberOptional).isPresent();
        familyMemberOptional.ifPresent(familyMember1 -> assertFalse(familyMember1.isActive()));
    }

    // Using TestTransaction to commit the update query
    @Test
    void testUpdateIsActiveByMemberLoginId() {
        FamilyMember familyMember = saveFamilyMember();
        familyMemberRepository.updateIsActiveByMemberLoginId(familyMember.getMemberLoginId(), false);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        Optional<FamilyMember> familyMemberOptional = familyMemberRepository.findById(familyMember.getFamilyMemberId());

        assertThat(familyMemberOptional).isPresent();
        familyMemberOptional.ifPresent(familyMember1 -> assertFalse(familyMember1.isActive()));
    }

    private FamilyMember saveFamilyMember() {
        FamilyMember familyMember = getFamilyMember();
        return familyMemberRepository.save(familyMember);
    }

    private FamilyMember getFamilyMember() {
        return FamilyMember.builder()
                .memberLoginId(UUID.randomUUID().toString())
                .name(TestData.MEMBER_NAME)
                .familyGroup(familyGroup)
                .isActive(true)
                .build();
    }
}
