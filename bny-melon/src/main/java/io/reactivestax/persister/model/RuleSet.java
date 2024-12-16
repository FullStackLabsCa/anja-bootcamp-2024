package io.reactivestax.persister.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class RuleSet {
    private Long ruleSetId;
    private String ruleNo;
    private String ruleValue;
    @Setter
    private Long leftId;
    @Setter
    private Long rightId;
    private String root;
}