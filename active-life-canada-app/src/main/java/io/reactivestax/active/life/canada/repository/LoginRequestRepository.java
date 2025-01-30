package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.LoginRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoginRequestRepository extends JpaRepository<LoginRequest, UUID> {

    Optional<LoginRequest> findByLoginToken(String loginToken);
}
