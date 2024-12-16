package io.reactivestax.parser;

import io.reactivestax.parser.service.RuleSetFileCreatorService;

public class Main {
    public static void main(String[] args) {
        RuleSetFileCreatorService ruleSetFileProcessorService = new RuleSetFileCreatorService();
        ruleSetFileProcessorService.readAndCreateRuleSetFile();
    }
}