package io.reactivestax.parser.repository.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Table(name = "rule_set")
public class RuleSet {
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

    @Column(name = "root")
    private String root;
}
