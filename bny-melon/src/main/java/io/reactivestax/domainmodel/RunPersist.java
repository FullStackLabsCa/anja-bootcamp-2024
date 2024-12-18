package io.reactivestax.domainmodel;

import io.reactivestax.domainmodel.service.RuleSetFileProcessorService;

public class RunPersist {
    public static void main(String[] args) {
        RuleSetFileProcessorService ruleSetFileProcessorService = new RuleSetFileProcessorService();
        ruleSetFileProcessorService.readAndProcessRuleSetFile();
    }
}