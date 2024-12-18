package io.reactivestax.withoutdomainmodel.parser.service;

import io.reactivestax.withoutdomainmodel.parser.model.RuleSet;
import io.reactivestax.withoutdomainmodel.parser.repository.RuleSetRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class RuleSetFileCreatorService implements RuleSetFileCreator {
    private final RuleSetRepository ruleSetRepository = new RuleSetRepository();
    private final Logger logger = Logger.getLogger(RuleSetFileCreatorService.class.getName());

    @Override
    public void readAndCreateRuleSetFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/resources/parsed/ruleset.data"))) {
            fetchRuleSets().forEach(rs -> {
                try {
                    bw.write(rs.getRuleNo() + rs.getRuleValue());
                    bw.newLine();
                } catch (IOException e) {
                    logger.info(e.getMessage());
                }
            });
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    private List<RuleSet> fetchRuleSets() {
        return ruleSetRepository.readRuleSet();
    }
}
