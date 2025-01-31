package io.reactivestax.active.life.canada.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityFunctionId implements Serializable {

    private Long function;
    private Long facility;
}