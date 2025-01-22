package io.reactivestax.message.processor.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.reactivestax.message.processor.enums.OtpLock;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID customerId;

    private String firstName;
    private String lastName;

    @JsonBackReference
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts;

    @Enumerated(value = EnumType.STRING)
    private OtpLock otpLock = OtpLock.NOT_LOCKED;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
