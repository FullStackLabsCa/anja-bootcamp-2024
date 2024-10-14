package io.reactivestax.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "securities_reference")
public class SecuritiesReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cusip")
    private String cusip;
}
