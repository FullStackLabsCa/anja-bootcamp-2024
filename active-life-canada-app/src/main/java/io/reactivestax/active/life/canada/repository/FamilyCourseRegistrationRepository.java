package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.FamilyCourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FamilyCourseRegistrationRepository extends JpaRepository<FamilyCourseRegistration, UUID> {

    Optional<FamilyCourseRegistration> findByFamilyCourseRegistrationIdAndIsWithdrawnFalseAndEnrollmentActorIdOrFamilyMember_FamilyMemberId
            (UUID familyCourseRegistrationId, UUID enrollmentActorId, UUID familyMemberId);

    List<FamilyCourseRegistration> findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId(UUID enrollmentActorId, UUID familyMemberId);

    boolean existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn(UUID familyMemberId, UUID offeredCourseId, boolean isWithdrawn);
}
