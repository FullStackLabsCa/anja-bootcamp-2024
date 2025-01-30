package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.FamilyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FamilyGroupRepository extends JpaRepository<FamilyGroup, UUID> {
}
