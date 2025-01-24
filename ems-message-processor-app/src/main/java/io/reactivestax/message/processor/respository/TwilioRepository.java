package io.reactivestax.message.processor.respository;

import io.reactivestax.message.processor.domain.TwilioCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TwilioRepository extends JpaRepository<TwilioCredentials, UUID> {
}
