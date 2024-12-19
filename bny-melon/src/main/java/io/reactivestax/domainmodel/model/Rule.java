package io.reactivestax.domainmodel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Rule {
    private long ruleSetId;
    private String ruleNo;
    private String ruleValue;
    private long leftId;
    private long rightId;
}
