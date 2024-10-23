package io.reactivestax.consumer.type.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "securities_reference")
public class SecuritiesReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cusip", nullable = false, unique = true)
    private String cusip;
}
