package io.reactivestax.active.life.canada.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class AuditTrail {
    @CreationTimestamp
    private LocalDateTime createdTs;
    private String createdBy;
    @UpdateTimestamp
    private LocalDateTime lastUpdatedTs;
    private String lastUpdatedBy;
}
