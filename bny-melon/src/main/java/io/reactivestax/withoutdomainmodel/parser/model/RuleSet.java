package io.reactivestax.withoutdomainmodel.parser.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RuleSet {
    private String ruleNo;
    private String ruleValue;
}