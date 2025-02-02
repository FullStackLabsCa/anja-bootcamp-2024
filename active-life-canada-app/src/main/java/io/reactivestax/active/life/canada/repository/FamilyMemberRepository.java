package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {
    Optional<FamilyMember> findByFamilyMemberIdAndIsActive(UUID familyMemberId, boolean isActive);

    Optional<FamilyMember> findByMemberLoginId(String memberLoginId);

    boolean existsByFamilyMemberIdAndIsActive(UUID familyMemberId, boolean isActive);

    boolean existsByMemberLoginId(String memberLoginId);

    Optional<FamilyMember> findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
            (String memberLoginId, boolean isActive, UUID familyGroupId);

    boolean existsByMemberLoginIdAndFamilyGroup_FamilyGroupId(String memberLoginId, UUID familyGroupId);

    @Modifying
    @Query("UPDATE FamilyMember fm SET fm.isActive = ?2 WHERE fm.familyMemberId = ?1")
    void updateIsActiveByFamilyMemberId(UUID familyMemberId, boolean isActive);

    @Modifying
    @Query("UPDATE FamilyMember fm SET fm.isActive = ?2 WHERE fm.memberLoginId = ?1")
    void updateIsActiveByMemberLoginId(String memberLoginId, boolean isActive);
}
