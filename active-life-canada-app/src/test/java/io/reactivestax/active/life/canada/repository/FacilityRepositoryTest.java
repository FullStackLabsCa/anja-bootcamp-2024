package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.Facility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class FacilityRepositoryTest {
    @Autowired
    private FacilityRepository facilityRepository;

    @Test
    void testFindById() {
        Optional<Facility> facilityOptional = facilityRepository.findById(1L);
        assertThat(facilityOptional).isPresent();
        facilityOptional.ifPresent(facility -> assertEquals(1L, facility.getFacilityId()));
    }
}
