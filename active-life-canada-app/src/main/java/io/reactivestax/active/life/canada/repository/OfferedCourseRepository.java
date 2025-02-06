package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfferedCourseRepository extends JpaRepository<OfferedCourse, UUID> , JpaSpecificationExecutor<OfferedCourse> {
    Optional<OfferedCourse> findByBarCode(UUID barCode);

    Optional<OfferedCourse> findByBarCodeAndAvailableForEnrollment(UUID barCode, AvailableForEnrollment availableForEnrollment);

    List<OfferedCourse> findAllByAvailableForEnrollmentNot(AvailableForEnrollment availableForEnrollment);
}
