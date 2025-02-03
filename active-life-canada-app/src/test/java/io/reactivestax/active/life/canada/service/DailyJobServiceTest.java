package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class DailyJobServiceTest {

    @Autowired
    private DailyJobService dailyJobService;

    @MockitoBean
    private OfferedCourseRepository offeredCourseRepository;

    @MockitoBean
    private ActiveLifeUtil activeLifeUtil;

    private OfferedCourse offeredCourse1;

    @BeforeAll
    void setUp() {
        offeredCourse1 = OfferedCourse.builder()
                .offeredCourseId(TestData.OFFERED_COURSE_ID_UUID)
                .availableForEnrollment(AvailableForEnrollment.AVAILABLE)
                .startDate(LocalDate.now())
                .startTime(LocalTime.now().minusHours(1))
                .build();
    }

    @Test
    void testMarkOfferedCourseNotAvailableWhenNoCoursesNeedUpdate() {
        when(offeredCourseRepository.findAllByAvailableForEnrollmentNot(AvailableForEnrollment.NOT_AVAILABLE))
                .thenReturn(List.of());

        dailyJobService.markOfferedCourseNotAvailable();

        verify(offeredCourseRepository, never()).saveAll(anyList());
    }

    @Test
    void testMarkOfferedCourseNotAvailableWhenCoursesNeedUpdate() {
        OfferedCourse offeredCourse2 = new OfferedCourse();
        offeredCourse2.setOfferedCourseId(TestData.OFFERED_COURSE_ID_UUID);
        offeredCourse2.setAvailableForEnrollment(AvailableForEnrollment.AVAILABLE);
        offeredCourse2.setStartDate(LocalDate.now());
        offeredCourse2.setStartTime(LocalTime.now());

        List<OfferedCourse> offeredCourseList = List.of(offeredCourse1, offeredCourse2);
        when(offeredCourseRepository.findAllByAvailableForEnrollmentNot(AvailableForEnrollment.NOT_AVAILABLE))
                .thenReturn(offeredCourseList);
        when(activeLifeUtil.compareDateAndTime(any(LocalDate.class), any(LocalTime.class))).thenReturn(true);

        dailyJobService.markOfferedCourseNotAvailable();

        verify(offeredCourseRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testMarkOfferedCourseNotAvailableWhenCompareDateAndTimeReturnsFalse() {
        List<OfferedCourse> offeredCourseList = List.of(offeredCourse1);
        when(offeredCourseRepository.findAllByAvailableForEnrollmentNot(AvailableForEnrollment.NOT_AVAILABLE))
                .thenReturn(offeredCourseList);
        when(activeLifeUtil.compareDateAndTime(any(LocalDate.class), any(LocalTime.class))).thenReturn(false);

        dailyJobService.markOfferedCourseNotAvailable();

        verify(offeredCourseRepository, never()).saveAll(anyList());
    }
}
