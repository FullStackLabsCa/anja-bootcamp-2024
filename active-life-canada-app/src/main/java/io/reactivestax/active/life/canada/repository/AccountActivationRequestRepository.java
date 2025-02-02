package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.entity.AccountActivationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountActivationRequestRepository extends JpaRepository<AccountActivationRequest, UUID> {
    Optional<AccountActivationRequest> findByToken(UUID token);
}
