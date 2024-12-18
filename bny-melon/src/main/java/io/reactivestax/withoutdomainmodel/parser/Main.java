package io.reactivestax.withoutdomainmodel.parser;

import io.reactivestax.withoutdomainmodel.parser.service.RuleSetFileCreatorService;

public class Main {
    public static void main(String[] args) {
        RuleSetFileCreatorService ruleSetFileProcessorService = new RuleSetFileCreatorService();
        ruleSetFileProcessorService.readAndCreateRuleSetFile();
    }
}