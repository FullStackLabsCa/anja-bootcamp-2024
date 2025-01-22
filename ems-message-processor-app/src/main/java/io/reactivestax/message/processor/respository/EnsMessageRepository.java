package io.reactivestax.message.processor.respository;

import io.reactivestax.message.processor.domain.EnsMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnsMessageRepository extends JpaRepository<EnsMessage, UUID> {
}
