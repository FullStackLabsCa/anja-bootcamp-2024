package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
}
