package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
