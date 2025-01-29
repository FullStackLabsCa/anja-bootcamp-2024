package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.domain.FamilyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FamilyGroupRepository extends JpaRepository<FamilyGroup, UUID> {
}
