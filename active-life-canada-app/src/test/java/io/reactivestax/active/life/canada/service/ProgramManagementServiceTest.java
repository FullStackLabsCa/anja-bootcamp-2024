package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.CourseUpdateRequest;
import io.reactivestax.active.life.canada.dto.OfferCourseRequest;
import io.reactivestax.active.life.canada.dto.OfferedCourseDetailsResponse;
import io.reactivestax.active.life.canada.dto.OfferedCourseSearchRequest;
import io.reactivestax.active.life.canada.entity.Course;
import io.reactivestax.active.life.canada.entity.Facility;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.repository.CourseRepository;
import io.reactivestax.active.life.canada.repository.FacilityRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseFeeRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProgramManagementServiceTest {

    @Autowired
    private ProgramManagementService programManagementService;

    @MockitoBean
    private CourseRepository courseRepository;

    @MockitoBean
    private FacilityRepository facilityRepository;

    @MockitoBean
    private OfferedCourseRepository offeredCourseRepository;

    @MockitoBean
    private OfferedCourseFeeRepository offeredCourseFeeRepository;

    @MockitoBean
    private AsyncJobsService asyncJobsService;

    private OfferCourseRequest offerCourseRequest;

    @BeforeEach
    void setUp() {
        offerCourseRequest = OfferCourseRequest.builder()
                .courseId(1L)
                .facilityId(1L)
                .residentCourseFee(180)
                .nonResidentCourseFee(200)
                .build();
    }

    @Test
    void testOfferCourse_Success() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(new Course()));
        when(facilityRepository.findById(anyLong())).thenReturn(Optional.of(new Facility()));

        programManagementService.offerCourse(offerCourseRequest);

        verify(offeredCourseFeeRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testOfferCourse_InvalidCourse() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class, () -> programManagementService.offerCourse(offerCourseRequest));
    }

    @Test
    void testOfferCourse_InvalidFacility() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(new Course()));
        when(facilityRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class, () -> programManagementService.offerCourse(offerCourseRequest));
    }

    @Test
    void testUpdateOfferedCourse_Success() {
        CourseUpdateRequest courseUpdateRequest = CourseUpdateRequest.builder()
                .barCode(TestData.BAR_CODE_STRING).build();
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.of(OfferedCourse.builder()
                .barCode(TestData.BAR_CODE_UUID).noOfSpots(10).build()));
        when(offeredCourseRepository.save(any(OfferedCourse.class))).thenReturn(new OfferedCourse());

        programManagementService.updateOfferedCourse(courseUpdateRequest);

        verify(offeredCourseRepository, times(1)).save(any(OfferedCourse.class));
    }

    @Test
    void testUpdateOfferedCourse_Success_NoOfSpotsUpdated() {
        CourseUpdateRequest courseUpdateRequest = CourseUpdateRequest.builder()
                .barCode(TestData.BAR_CODE_STRING)
                .noOfSpots(20)
                .build();
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.of(OfferedCourse.builder()
                .offeredCourseId(TestData.OFFERED_COURSE_ID_UUID)
                .barCode(TestData.BAR_CODE_UUID)
                .noOfSpots(10)
                .course(Course.builder().name(TestData.COURSE_NAME).build())
                .build()));
        when(offeredCourseRepository.save(any(OfferedCourse.class))).thenReturn(new OfferedCourse());

        programManagementService.updateOfferedCourse(courseUpdateRequest);

        verify(offeredCourseRepository, times(1)).save(any(OfferedCourse.class));
        verify(asyncJobsService, times(1))
                .getAllWaitlistedMembersByOfferedCourseIdAndSendToEms(any(UUID.class), anyString());
    }

    @Test
    void testGetAllOfferedCourses() {
        when(offeredCourseRepository.findAll()).thenReturn(List.of(OfferedCourse.builder()
                .barCode(TestData.BAR_CODE_UUID).build()));

        List<OfferedCourseDetailsResponse> offeredCourseDetailsResponses = programManagementService.offeredCourses();
        assertEquals(1, offeredCourseDetailsResponses.size());
    }

    @Test
    void testUpdateOfferedCourse_InvalidOfferedCourse() {
        CourseUpdateRequest request = new CourseUpdateRequest(TestData.BAR_CODE_STRING, AvailableForEnrollment.AVAILABLE);
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class, () -> programManagementService.updateOfferedCourse(request));
    }

    @Test
    void testSearchOfferedCourses() {
        OfferedCourseSearchRequest request = new OfferedCourseSearchRequest();
        List<OfferedCourse> offeredCourses = List.of(OfferedCourse.builder()
                .barCode(TestData.BAR_CODE_UUID).build());

        when(offeredCourseRepository.findAll(any(Specification.class))).thenReturn(offeredCourses);

        List<OfferedCourseDetailsResponse> result = programManagementService.searchOfferedCourses(request);

        assertFalse(result.isEmpty());
    }
}
