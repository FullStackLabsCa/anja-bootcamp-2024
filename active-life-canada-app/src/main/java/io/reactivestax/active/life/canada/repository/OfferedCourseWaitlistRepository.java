package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.OfferedCourseWaitlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OfferedCourseWaitlistRepository extends JpaRepository<OfferedCourseWaitlist, UUID> {
    long countByOfferedCourse_OfferedCourseId(UUID offeredCourseId);

    List<OfferedCourseWaitlist> findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId(UUID enrollmentActorId, UUID familyMemberId);
}
