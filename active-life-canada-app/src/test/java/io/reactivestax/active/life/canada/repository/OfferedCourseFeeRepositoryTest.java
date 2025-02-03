package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.OfferedCourseFee;
import io.reactivestax.active.life.canada.enums.FeeType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class OfferedCourseFeeRepositoryTest {

    @Autowired
    private OfferedCourseFeeRepository offeredCourseFeeRepository;

    @Test
    void testSaveAll() {
        OfferedCourseFee offeredCourseFee1 = OfferedCourseFee.builder()
                .courseFee(180)
                .feeType(FeeType.RESIDENT)
                .build();
        OfferedCourseFee offeredCourseFee2 = OfferedCourseFee.builder()
                .courseFee(200)
                .feeType(FeeType.NON_RESIDENT)
                .build();
        List<OfferedCourseFee> offeredCourseFeeList = List.of(offeredCourseFee1, offeredCourseFee2);
        List<OfferedCourseFee> offeredCourseFees = offeredCourseFeeRepository.saveAll(offeredCourseFeeList);

        assertThat(offeredCourseFees).isNotNull();
        assertEquals(2, offeredCourseFees.size());
    }
}
