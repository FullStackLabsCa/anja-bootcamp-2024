package io.reactivestax.domainmodel.service;

import io.reactivestax.domainmodel.model.*;
import io.reactivestax.domainmodel.repository.RuleSetRepository;
import io.reactivestax.withoutdomainmodel.persister.enums.RuleNo;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RuleSetReaderService {
    private final RuleSetRepository ruleSetRepository = new RuleSetRepository();
    private final List<RuleSet> ruleSetList = new ArrayList<>();
    private final Map<RuleNo, Object> ruleSetMap = new EnumMap<>(RuleNo.class);

    public void readRulesAndProcess() {
        readRules().forEach(rule -> RuleNo.getEnumValue(rule.getRuleNo()).ifPresent(ruleNo -> {
            switch (ruleNo) {
                case ONE: {
                    RuleSet ruleSet = (RuleSet) rule;
                    ruleSetList.add(ruleSet);
                    ruleSetMap.put(ruleNo, ruleSet);
                    break;
                }
                case TWO: {
                    RuleSet ruleSet = (RuleSet) ruleSetMap.get(RuleNo.ONE);
                    ruleSet.addAccount((Account) rule);
                    break;
                }
                case THREE: {
                    RuleSet ruleSet = (RuleSet) ruleSetMap.get(RuleNo.ONE);
                    EligibilityGroup eligibilityGroup = (EligibilityGroup) rule;
                    ruleSet.addEligibilityGroup(eligibilityGroup);
                    ruleSetMap.put(RuleNo.THREE, eligibilityGroup);
                    break;
                }
                case FOUR: {
                    EligibilityGroup eligibilityGroup = (EligibilityGroup) ruleSetMap.get(RuleNo.THREE);
                    EligibilityRuleDetails eligibilityRuleDetails = (EligibilityRuleDetails) rule;
                    eligibilityGroup.addEligibilityRuleDetails(eligibilityRuleDetails);
                    ruleSetMap.put(RuleNo.THREE, eligibilityRuleDetails);
                    break;
                }
                case FIVE: {
                    EligibilityRuleDetails eligibilityRuleDetails =
                            (EligibilityRuleDetails) ruleSetMap.get(RuleNo.FOUR);
                    eligibilityRuleDetails.addSetValues((SetValues) rule);
                    break;
                }
                case SIX: {
                    EligibilityRuleDetails eligibilityRuleDetails =
                            (EligibilityRuleDetails) ruleSetMap.get(RuleNo.FOUR);
                    eligibilityRuleDetails.addConcentrationLimit((ConcentrationLimit) rule);
                    break;
                }
                case SEVEN: {
                    EligibilityGroup eligibilityGroup = (EligibilityGroup) ruleSetMap.get(RuleNo.THREE);
                    eligibilityGroup.addMarginCut((MarginCut) rule);
                    break;
                }
                default:
            }
        }));
        ruleSetMap.clear();
    }

    private void printRuleSet(){
        ruleSetList.forEach(System.out::println);
    }

    private List<Rule> readRules() {
        return ruleSetRepository.readRules();
    }
}
