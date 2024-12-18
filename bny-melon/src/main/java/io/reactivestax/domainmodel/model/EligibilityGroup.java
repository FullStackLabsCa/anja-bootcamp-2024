package io.reactivestax.domainmodel.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Getter
public class EligibilityGroup extends Rule {
    private final List<EligibilityRuleDetails> eligibilityRuleDetailsList = new ArrayList<>();
    private final List<MarginCut> marginCutList = new ArrayList<>();

    public void addEligibilityRuleDetails(EligibilityRuleDetails eligibilityRuleDetails) {
        eligibilityRuleDetailsList.add(eligibilityRuleDetails);
    }

    public void addMarginCut(MarginCut marginCut) {
        marginCutList.add(marginCut);
    }
}
