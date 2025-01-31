package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.OfferedCourseFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OfferedCourseFeeRepository extends JpaRepository<OfferedCourseFee, UUID> {
}
