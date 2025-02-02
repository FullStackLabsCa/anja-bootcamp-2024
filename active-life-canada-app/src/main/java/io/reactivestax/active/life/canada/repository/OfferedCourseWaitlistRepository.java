package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.OfferedCourseWaitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OfferedCourseWaitlistRepository extends JpaRepository<OfferedCourseWaitlist, UUID> {
    List<OfferedCourseWaitlist> findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId(UUID enrollmentActorId, UUID familyMemberId);

    boolean existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseId(UUID offeredCourseId, UUID familyMemberId);

    List<OfferedCourseWaitlist> findAllByOfferedCourse_OfferedCourseId(UUID offeredCourseId);

    @Modifying
    @Query("DELETE FROM OfferedCourseWaitlist wl WHERE wl.offeredCourse.offeredCourseId = ?1 AND wl.familyMember.familyMemberId = ?2")
    void deleteFromWaitlistByFamilyMemberIdAndOfferedCourseId(UUID offeredCourseId, UUID familyMemberId);
}
