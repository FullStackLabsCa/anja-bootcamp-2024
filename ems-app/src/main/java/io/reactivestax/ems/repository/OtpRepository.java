package io.reactivestax.ems.repository;

import io.reactivestax.ems.domain.OtpMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<OtpMessage, UUID> {

    @Query("SELECT om FROM OtpMessage om WHERE om.customerId = ?1")
    List<OtpMessage> findAllByCustomerId(String customerId);
}
