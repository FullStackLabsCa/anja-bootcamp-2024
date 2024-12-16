package io.reactivestax.persister;

import io.reactivestax.persister.service.RuleSetFileProcessorService;

public class Main {
    public static void main(String[] args) {
        RuleSetFileProcessorService ruleSetFileProcessorService = new RuleSetFileProcessorService();
        ruleSetFileProcessorService.readAndProcessRuleSetFile();
    }
}