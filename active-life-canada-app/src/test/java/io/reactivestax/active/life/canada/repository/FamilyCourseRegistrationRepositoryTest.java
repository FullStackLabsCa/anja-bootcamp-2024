package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.entity.*;
import io.reactivestax.active.life.canada.enums.Status;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FamilyCourseRegistrationRepositoryTest {

    @Autowired
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    @Autowired
    private FamilyGroupRepository familyGroupRepository;
    @Autowired
    private FacilityRepository facilityRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    private FamilyCourseRegistration savedFamilyCourseRegistration;
    private FamilyMember savedFamilyMember;
    private OfferedCourse savedOfferedCourse;

    @BeforeAll
    void setUp() {
        FamilyMember familyMember = FamilyMember.builder()
                .name(TestData.MEMBER_NAME)
                .memberLoginId(TestData.MEMBER_LOGIN_ID)
                .build();

        FamilyGroup familyGroup = FamilyGroup.builder()
                .familyMembers(List.of(familyMember))
                .familyPin(TestData.PASSWORD)
                .credits(0.0)
                .failedLoginAttempts(0)
                .status(Status.ACTIVE)
                .build();
        familyMember.setFamilyGroup(familyGroup);
        FamilyGroup savedFamilyGroup = familyGroupRepository.save(familyGroup);
        savedFamilyMember = savedFamilyGroup.getFamilyMembers().get(0);

        Course course = courseRepository.findAll().get(0);
        Facility facility = facilityRepository.findAll().get(0);

        OfferedCourse offeredCourse = OfferedCourse.builder()
                .barCode(TestData.BAR_CODE_UUID)
                .course(course).facility(facility).build();
        savedOfferedCourse = offeredCourseRepository.save(offeredCourse);

        FamilyCourseRegistration familyCourseRegistration = FamilyCourseRegistration.builder()
                .familyMember(savedFamilyMember)
                .isWithdrawn(false)
                .cost(100)
                .withdrawnCredits(0)
                .offeredCourse(offeredCourse)
                .enrollmentActorId(familyMember.getFamilyMemberId())
                .enrollmentDate(LocalDate.now())
                .build();
        savedFamilyCourseRegistration = familyCourseRegistrationRepository.save(familyCourseRegistration);
    }

    @Test
    void testExistsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn() {
        boolean boo = familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn
                (savedFamilyMember.getFamilyMemberId(), savedOfferedCourse.getOfferedCourseId(), false);
        assertTrue(boo);
    }

    @Test
    void testFindByFamilyCourseRegistrationIdAndIsWithdrawnFalseAndEnrollmentActorIdOrFamilyMember_FamilyMemberId() {
        Optional<FamilyCourseRegistration> familyCourseRegistrationOptional = familyCourseRegistrationRepository
                .findByFamilyCourseRegistrationIdAndIsWithdrawnFalseAndEnrollmentActorIdOrFamilyMember_FamilyMemberId
                        (savedFamilyCourseRegistration.getFamilyCourseRegistrationId(),
                                savedFamilyCourseRegistration.getEnrollmentActorId(), savedFamilyMember.getFamilyMemberId());

        assertThat(familyCourseRegistrationOptional).isPresent();
    }

    @Test
    void testFindAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId() {
        List<FamilyCourseRegistration> familyCourseRegistrationList = familyCourseRegistrationRepository.findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId
                (savedFamilyCourseRegistration.getEnrollmentActorId(), savedFamilyMember.getFamilyMemberId());

        assertEquals(1, familyCourseRegistrationList.size());
    }
}
