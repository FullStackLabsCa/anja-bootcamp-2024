package io.reactivestax.domainmodel.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "rule_set")
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_set_id")
    private Long ruleSetId;

    @Column(name = "rule_no")
    private String ruleNo;

    @Column(name = "rule_value")
    private String ruleValue;

    @Column(name = "left_id")
    private Long leftId;

    @Column(name = "right_id")
    private Long rightId;
}
