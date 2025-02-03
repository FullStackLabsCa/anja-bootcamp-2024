package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.Course;
import io.reactivestax.active.life.canada.entity.Facility;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OfferedCourseRepositoryTest {

    private Course course;
    private Facility facility;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private FacilityRepository facilityRepository;
    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @BeforeAll()
    void setUp() {
        course = courseRepository.findAll().get(0);
        facility = facilityRepository.findAll().get(0);
    }

    @Test
    void testSave() {
        OfferedCourse offeredCourse = getOfferedCourse();
        OfferedCourse savedOfferedCourse = offeredCourseRepository.save(offeredCourse);

        assertThat(savedOfferedCourse).isNotNull();
        assertThat(savedOfferedCourse.getOfferedCourseId()).isNotNull();
    }

    @Test
    void testSaveAll() {
        OfferedCourse offeredCourse1 = getOfferedCourse();
        OfferedCourse offeredCourse2 = getOfferedCourse();
        List<OfferedCourse> offeredCourses = offeredCourseRepository.saveAll(List.of(offeredCourse1, offeredCourse2));

        assertThat(offeredCourses).isNotNull();
        assertEquals(2, offeredCourses.size());
    }

    @Test
    void testFindAll() {
        OfferedCourse offeredCourse1 = getOfferedCourse();
        OfferedCourse offeredCourse2 = getOfferedCourse();
        offeredCourseRepository.saveAll(List.of(offeredCourse1, offeredCourse2));
        List<OfferedCourse> offeredCourseList = offeredCourseRepository.findAll();

        assertThat(offeredCourseList).isNotNull();
        assertTrue(offeredCourseList.size() >= 2);
    }

    @Test
    void findByBarCode() {
        OfferedCourse offeredCourse = getOfferedCourse();
        offeredCourseRepository.save(offeredCourse);
        Optional<OfferedCourse> byBarCode = offeredCourseRepository.findByBarCode(offeredCourse.getBarCode());

        assertThat(byBarCode).isPresent();
        byBarCode.ifPresent(offeredCourse1 -> {
            assertThat(offeredCourse1.getOfferedCourseId()).isNotNull();
            assertEquals(offeredCourse1.getBarCode(), offeredCourse.getBarCode());
        });
    }

    @Test
    void findAllByAvailableForEnrollmentNot() {
        OfferedCourse offeredCourse = getOfferedCourse();
        offeredCourseRepository.save(offeredCourse);
        List<OfferedCourse> allByAvailableForEnrollmentNot = offeredCourseRepository.findAllByAvailableForEnrollmentNot(AvailableForEnrollment.AVAILABLE);

        assertTrue(allByAvailableForEnrollmentNot.isEmpty());
    }

    private OfferedCourse getOfferedCourse() {
        return OfferedCourse.builder()
                .barCode(UUID.randomUUID())
                .course(course)
                .facility(facility)
                .noOfSpots(10)
                .availableForEnrollment(AvailableForEnrollment.AVAILABLE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .startTime(LocalTime.now())
                .endTime(LocalTime.now().plusHours(2))
                .isAllDayCourse(true)
                .build();
    }
}
