package io.reactivestax.ems.repository;

import io.reactivestax.ems.domain.ens.EnsMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnsRepository extends JpaRepository<EnsMessage, Long> {
}
