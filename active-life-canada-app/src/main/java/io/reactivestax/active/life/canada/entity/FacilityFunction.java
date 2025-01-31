package io.reactivestax.active.life.canada.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacilityFunction extends AuditTrail {

    @EmbeddedId
    private FacilityFunctionId id;

    @ManyToOne
    @MapsId("function")
    @JoinColumn(name = "function_id", nullable = false)
    private Function function;

    @ManyToOne
    @MapsId("facility")
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;
}
