package io.reactivestax.entity;

import jakarta.persistence.Column;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
public class CustomTimestamp {
    @CreationTimestamp
    @Column(name = "created_timestamp", updatable = false, nullable = false)
    private Timestamp createdTimestamp;

    @UpdateTimestamp
    @Column(name = "updated_timestamp", nullable = false)
    private Timestamp updatedTimestamp;
}
