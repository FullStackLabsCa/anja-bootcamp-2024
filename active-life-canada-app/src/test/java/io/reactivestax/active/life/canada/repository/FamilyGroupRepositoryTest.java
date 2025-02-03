package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.entity.FamilyGroup;
import io.reactivestax.active.life.canada.enums.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class FamilyGroupRepositoryTest {

    @Autowired
    private FamilyGroupRepository familyGroupRepository;

    @Test
    void testSave() {
        FamilyGroup familyGroup = FamilyGroup.builder().status(Status.ACTIVE).familyPin(TestData.PASSWORD).build();
        FamilyGroup savedFamilyGroup = familyGroupRepository.save(familyGroup);
        assertThat(savedFamilyGroup).isNotNull();
        assertThat(savedFamilyGroup.getFamilyGroupId()).isNotNull();
    }
}
