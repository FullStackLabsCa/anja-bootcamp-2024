package io.reactivestax.ems.repository;

import io.reactivestax.ems.domain.OtpMessage;
import io.reactivestax.ems.enums.OtpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<OtpMessage, UUID> {

    @Query("SELECT om FROM OtpMessage om WHERE om.customerId = ?1 AND om.otpStatus = ?2")
    List<OtpMessage> findAllByCustomerIdAndOtpStatus(String customerId, OtpStatus otpStatus);

    @Query("SELECT om FROM OtpMessage om WHERE om.customerId = ?1 AND om.otpStatus NOT IN ('DISCARDED', 'VERIFIED')")
    List<OtpMessage> findNotDiscardedAndNotVerifiedOtpMessageByCustomerId(String customerId);
}
