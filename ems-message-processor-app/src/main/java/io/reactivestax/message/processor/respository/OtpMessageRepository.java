package io.reactivestax.message.processor.respository;

import io.reactivestax.message.processor.domain.OtpMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OtpMessageRepository extends JpaRepository<OtpMessage, UUID> {
}
