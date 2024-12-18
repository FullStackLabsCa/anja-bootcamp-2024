package io.reactivestax.domainmodel;

import io.reactivestax.domainmodel.service.RuleSetReaderService;

public class RunParser {
    public static void main(String[] args) {
        RuleSetReaderService ruleSetReaderService = new RuleSetReaderService();
        ruleSetReaderService.readRulesAndProcess();
    }
}