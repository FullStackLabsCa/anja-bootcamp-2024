package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.FamilyCourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FamilyCourseRegistrationRepository extends JpaRepository<FamilyCourseRegistration, UUID> {

    @Query("SELECT fcr from FamilyCourseRegistration fcr where fcr.familyCourseRegistrationId = ?1 AND (fcr" +
            ".enrollmentActorId = ?2 OR fcr.familyMember.familyMemberId = ?2) AND fcr.isWithdrawn = false")
    Optional<FamilyCourseRegistration> findByFamilyCourseRegistrationIdAndEnrollmentActorIdOrFamilyMemberIdForNonWithdrawnCourse
            (UUID familyCourseRegistrationId, UUID memberId);

    @Query("SELECT fcr from FamilyCourseRegistration fcr where fcr.enrollmentActorId = ?1 OR fcr.familyMember.familyMemberId = ?1")
    List<FamilyCourseRegistration> findByEnrollmentActorIdOrFamilyMemberId(UUID familyMemberId);
}
