package io.reactivestax.domainmodel.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Getter
public class RuleSet extends Rule {
    private final List<Account> accountList = new ArrayList<>();
    private final List<EligibilityGroup> eligibilityGroupList = new ArrayList<>();

    public void addAccount(Account account) {
        accountList.add(account);
    }

    public void addEligibilityGroup(EligibilityGroup eligibilityGroup) {
        eligibilityGroupList.add(eligibilityGroup);
    }
}