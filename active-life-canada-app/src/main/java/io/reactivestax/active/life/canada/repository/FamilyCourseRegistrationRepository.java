package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.FamilyCourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FamilyCourseRegistrationRepository extends JpaRepository<FamilyCourseRegistration, UUID> {
}
