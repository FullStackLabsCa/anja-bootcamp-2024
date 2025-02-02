package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DailyJobService {

    private final OfferedCourseRepository offeredCourseRepository;
    private final ActiveLifeUtil activeLifeUtil;

    public DailyJobService(OfferedCourseRepository offeredCourseRepository,
                           ActiveLifeUtil activeLifeUtil) {
        this.offeredCourseRepository = offeredCourseRepository;
        this.activeLifeUtil = activeLifeUtil;
    }

    @Scheduled(cron = "0 1 0 * * *")
    public void markOfferedCourseNotAvailable() {
        log.info("Running daily job: markOfferedCourseNotAvailable");
        List<OfferedCourse> updatedOfferedCourses = new ArrayList<>();
        List<OfferedCourse> offeredCourseList = offeredCourseRepository.findAllByAvailableForEnrollmentNot(AvailableForEnrollment.NOT_AVAILABLE);
        offeredCourseList.forEach(offeredCourse -> {
            if (activeLifeUtil.compareDateAndTime(offeredCourse.getStartDate(), offeredCourse.getStartTime())) {
                offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.NOT_AVAILABLE);
                updatedOfferedCourses.add(offeredCourse);
            }
        });
        offeredCourseRepository.saveAll(updatedOfferedCourses);
    }
}
