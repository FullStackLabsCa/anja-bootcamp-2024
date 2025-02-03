package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void testFindById() {
        Optional<Course> courseOptional = courseRepository.findById(1L);
        assertThat(courseOptional).isPresent();
        courseOptional.ifPresent(course -> assertEquals(1L, course.getCourseId()));
    }
}
