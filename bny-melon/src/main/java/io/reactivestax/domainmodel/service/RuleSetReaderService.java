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
                case ONE -> prepareRuleSet(rule, ruleNo);
                case TWO -> prepareAccount(rule);
                case THREE -> prepareEligibilityGroup(rule);
                case FOUR -> prepareEligibilityGroupDetails(rule);
                case FIVE -> prepareSetValues(rule);
                case SIX -> prepareConcentrationLimit(rule);
                case SEVEN -> prepareMarginCut(rule);
            }
        }));
        ruleSetMap.clear();
        printRuleSet();
    }

    private void prepareMarginCut(Rule rule) {
        EligibilityGroup eligibilityGroup = (EligibilityGroup) ruleSetMap.get(RuleNo.THREE);
        MarginCut marginCut = new MarginCut();
        marginCut.setRuleNo(rule.getRuleNo());
        marginCut.setRuleValue(rule.getRuleValue());
        eligibilityGroup.addMarginCut(marginCut);
    }

    private void prepareConcentrationLimit(Rule rule) {
        EligibilityRuleDetails eligibilityRuleDetails =
                (EligibilityRuleDetails) ruleSetMap.get(RuleNo.FOUR);
        ConcentrationLimit concentrationLimit = new ConcentrationLimit();
        concentrationLimit.setRuleNo(rule.getRuleNo());
        concentrationLimit.setRuleValue(rule.getRuleValue());
        eligibilityRuleDetails.addConcentrationLimit(concentrationLimit);
    }

    private void prepareSetValues(Rule rule) {
        EligibilityRuleDetails eligibilityRuleDetails =
                (EligibilityRuleDetails) ruleSetMap.get(RuleNo.FOUR);
        SetValues setValues = new SetValues();
        setValues.setRuleNo(rule.getRuleNo());
        setValues.setRuleValue(rule.getRuleValue());
        eligibilityRuleDetails.addSetValues(setValues);
    }

    private void prepareEligibilityGroupDetails(Rule rule) {
        EligibilityGroup eligibilityGroup = (EligibilityGroup) ruleSetMap.get(RuleNo.THREE);
        EligibilityRuleDetails eligibilityRuleDetails = new EligibilityRuleDetails();
        eligibilityRuleDetails.setRuleNo(rule.getRuleNo());
        eligibilityRuleDetails.setRuleValue(rule.getRuleValue());
        eligibilityGroup.addEligibilityRuleDetails(eligibilityRuleDetails);
        ruleSetMap.put(RuleNo.FOUR, eligibilityRuleDetails);
    }

    private void prepareEligibilityGroup(Rule rule) {
        RuleSet ruleSet = (RuleSet) ruleSetMap.get(RuleNo.ONE);
        EligibilityGroup eligibilityGroup = new EligibilityGroup();
        eligibilityGroup.setRuleNo(rule.getRuleNo());
        eligibilityGroup.setRuleValue(rule.getRuleValue());
        ruleSet.addEligibilityGroup(eligibilityGroup);
        ruleSetMap.put(RuleNo.THREE, eligibilityGroup);
    }

    private void prepareAccount(Rule rule) {
        RuleSet ruleSet = (RuleSet) ruleSetMap.get(RuleNo.ONE);
        Account account = new Account();
        account.setRuleNo(rule.getRuleNo());
        account.setRuleValue(rule.getRuleValue());
        ruleSet.addAccount(account);
    }

    private void prepareRuleSet(Rule rule, RuleNo ruleNo) {
        RuleSet ruleSet = new RuleSet();
        ruleSet.setRuleNo(rule.getRuleNo());
        ruleSet.setRuleValue(rule.getRuleValue());
        ruleSetList.add(ruleSet);
        ruleSetMap.put(ruleNo, ruleSet);
    }

    private void printRuleSet() {
        ruleSetList.forEach(System.out::println);
    }

    private List<Rule> readRules() {
        return ruleSetRepository.readRules();
    }
}
