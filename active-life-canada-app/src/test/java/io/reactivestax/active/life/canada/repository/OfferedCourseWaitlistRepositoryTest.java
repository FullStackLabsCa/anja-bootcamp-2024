package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.entity.*;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import io.reactivestax.active.life.canada.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.transaction.TestTransaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class OfferedCourseWaitlistRepositoryTest {

    @Autowired
    private OfferedCourseWaitlistRepository offeredCourseWaitlistRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private FamilyGroupRepository familyGroupRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testFindAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId() {
        OfferedCourseWaitlist offeredCourseWaitlist = saveOfferedCourseWaitlist();
        List<OfferedCourseWaitlist> offeredCourseWaitlists = offeredCourseWaitlistRepository.findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId
                (offeredCourseWaitlist.getEnrollmentActorId(), offeredCourseWaitlist.getFamilyMember().getFamilyMemberId());

        assertThat(offeredCourseWaitlists).isNotNull();
        assertEquals(1, offeredCourseWaitlists.size());
    }

    @Test
    void testExistsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseId() {
        OfferedCourseWaitlist offeredCourseWaitlist = saveOfferedCourseWaitlist();
        boolean exists = offeredCourseWaitlistRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseId
                (offeredCourseWaitlist.getFamilyMember().getFamilyMemberId(),
                        offeredCourseWaitlist.getOfferedCourse().getOfferedCourseId());

        assertTrue(exists);
    }

    @Test
    void testFindAllByOfferedCourse_OfferedCourseId() {
        OfferedCourseWaitlist offeredCourseWaitlist = saveOfferedCourseWaitlist();
        List<OfferedCourseWaitlist> allByOfferedCourseOfferedCourseId = offeredCourseWaitlistRepository.findAllByOfferedCourse_OfferedCourseId
                (offeredCourseWaitlist.getOfferedCourse().getOfferedCourseId());

        assertThat(allByOfferedCourseOfferedCourseId).isNotNull();
        assertEquals(1, allByOfferedCourseOfferedCourseId.size());
    }

    @Test
    void testDeleteFromWaitlistByOfferedCourseIdAndFamilyMemberId() {
        OfferedCourseWaitlist offeredCourseWaitlist = saveOfferedCourseWaitlist();
        offeredCourseWaitlistRepository.deleteFromWaitlistByOfferedCourseIdAndFamilyMemberId
                (offeredCourseWaitlist.getOfferedCourse().getOfferedCourseId(),
                        offeredCourseWaitlist.getFamilyMember().getFamilyMemberId());
        TestTransaction.flagForCommit();
        TestTransaction.end();

        Optional<OfferedCourseWaitlist> courseWaitlist = offeredCourseWaitlistRepository
                .findById(offeredCourseWaitlist.getCourseWaitListId());

        assertThat(courseWaitlist).isNotPresent();
    }

    private OfferedCourseWaitlist saveOfferedCourseWaitlist() {
        FamilyGroup familyGroup = FamilyGroup.builder()
                .failedLoginAttempts(0)
                .status(Status.ACTIVE)
                .familyPin(TestData.PASSWORD)
                .credits(0.0)
                .build();
        FamilyGroup savedFamilyGroup = familyGroupRepository.save(familyGroup);

        FamilyMember familyMember = FamilyMember.builder()
                .isActive(true)
                .name(TestData.MEMBER_NAME)
                .memberLoginId(UUID.randomUUID().toString())
                .emailId(TestData.EMAIL)
                .businessPhone(TestData.PHONE)
                .preferredModeOfCommunication(PreferredModeOfCommunication.BUSINESS_PHONE)
                .familyGroup(savedFamilyGroup)
                .build();
        FamilyMember savedFamilyMember = familyMemberRepository.save(familyMember);

        Course course = courseRepository.findAll().get(0);
        Facility facility = facilityRepository.findAll().get(0);

        OfferedCourse offeredCourse = OfferedCourse.builder()
                .barCode(UUID.randomUUID())
                .isAllDayCourse(true)
                .course(course)
                .facility(facility)
                .availableForEnrollment(AvailableForEnrollment.AVAILABLE)
                .registrationStartDate(LocalDate.now().minusDays(3))
                .startDate(LocalDate.now().minusDays(2))
                .endDate(LocalDate.now().plusDays(5)).build();
        OfferedCourse savedOfferedCourse = offeredCourseRepository.save(offeredCourse);

        OfferedCourseWaitlist build = OfferedCourseWaitlist.builder()
                .offeredCourse(savedOfferedCourse)
                .familyMember(savedFamilyMember)
                .enrollmentActorId(savedFamilyMember.getFamilyMemberId())
                .build();
        return offeredCourseWaitlistRepository.save(build);
    }
}
