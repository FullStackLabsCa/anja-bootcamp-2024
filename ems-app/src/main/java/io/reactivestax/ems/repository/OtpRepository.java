package io.reactivestax.ems.repository;

import io.reactivestax.ems.domain.OtpMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<OtpMessage, Long> {
}
