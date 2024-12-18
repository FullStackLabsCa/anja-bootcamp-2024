package io.reactivestax.withoutdomainmodel.persister;

import io.reactivestax.withoutdomainmodel.persister.service.RuleSetFileProcessorService;

public class Main {
    public static void main(String[] args) {
        RuleSetFileProcessorService ruleSetFileProcessorService = new RuleSetFileProcessorService();
        ruleSetFileProcessorService.readAndProcessRuleSetFile();
    }
}