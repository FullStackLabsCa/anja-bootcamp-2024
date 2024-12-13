package io.reactivestax;

import io.reactivestax.service.RuleSetFileProcessorService;

public class Main {
    public static void main(String[] args) {
        RuleSetFileProcessorService ruleSetFileProcessorService = new RuleSetFileProcessorService();
        ruleSetFileProcessorService.readRuleSetFile();
    }
}