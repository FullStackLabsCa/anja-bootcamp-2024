package io.reactivestax.active.life.canada.domain;

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
    private LocalDateTime createdAt;
    private LocalDateTime createdBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime updatedBy;
}
