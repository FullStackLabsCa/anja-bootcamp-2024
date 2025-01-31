package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.OfferedCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OfferedCourseRepository extends JpaRepository<OfferedCourse, UUID> {
    Optional<OfferedCourse> findByBarCode(UUID barCode);
}
