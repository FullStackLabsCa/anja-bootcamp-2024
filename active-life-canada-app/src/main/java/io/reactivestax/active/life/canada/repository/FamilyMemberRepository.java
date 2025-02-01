package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {
    Optional<FamilyMember> findByMemberLoginId(String memberLoginId);

    Optional<FamilyMember> findByMemberLoginIdAndFamilyGroup_FamilyGroupId(String memberLoginId, UUID familyGroupId);

    @Modifying
    @Query("UPDATE FamilyMember SET isActive = true WHERE activationToken = ?1")
    int activateAccountByActivationToken(String activationToken);

    Optional<FamilyMember> findByActivationToken(String activationToken);
}
