package io.reactivestax.ems.repository;

import io.reactivestax.ems.domain.OtpMessage;
import io.reactivestax.ems.enums.OtpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<OtpMessage, UUID> {

    @Query("SELECT om FROM OtpMessage om WHERE om.customerId = ?1")
    List<OtpMessage> findAllByCustomerId(String customerId);

    @Modifying
    @Query("UPDATE OtpMessage om SET om.otpStatus = ?1 WHERE om.customerId = ?2 AND om.otpStatus != ?1")
    void updateOtpStatusByCustomerIdAndOtpStatus(OtpStatus otpStatus, String customerId);

    @Query("SELECT om FROM OtpMessage om WHERE om.customerId = ?1 AND om.otpStatus = ?2")
    Optional<OtpMessage> findByCustomerIdAndOtpStatus(String customerId, OtpStatus otpStatus);
}
