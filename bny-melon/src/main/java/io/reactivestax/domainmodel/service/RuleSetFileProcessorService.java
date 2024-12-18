package io.reactivestax.domainmodel.service;

import io.reactivestax.domainmodel.enums.RuleType;
import io.reactivestax.domainmodel.model.Rule;
import io.reactivestax.domainmodel.repository.RuleSetRepository;
import io.reactivestax.domainmodel.util.DbUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static io.reactivestax.withoutdomainmodel.persister.enums.RuleNo.ONE;
import static io.reactivestax.withoutdomainmodel.persister.enums.RuleNo.THREE;

public class RuleSetFileProcessorService implements RuleSetFileProcessor {
    private long id = 0;
    private final Deque<Rule> ruleStack = new ArrayDeque<>();
    private List<Rule> ruleList = new ArrayList<>();
    private final RuleSetRepository ruleSetRepository = new RuleSetRepository();
    private final Logger logger = Logger.getLogger(RuleSetFileProcessorService.class.getName());

    @Override
    public void readAndProcessRuleSetFile() {
        try (Stream<String> stream = Files.lines(Path.of("src/main/resources/rulesetfiles/bony_smb_ruleset_d100914.data"))) {
            stream.forEach(line -> {
                String ruleNo = line.substring(0, 2);
                String ruleValue = line.substring(2);
                Rule rule = Rule.builder()
                        .ruleNo(ruleNo)
                        .ruleValue(ruleValue)
                        .build();
                prepareRuleSetList(rule);
                ruleList.add(rule);
            });
            processRuleSet();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    private void prepareRuleSetList(Rule rule) {
        RuleType.getEnumValue(rule.getRuleNo()).ifPresent(ruleType -> {
            switch (ruleType) {
                case ONE: {
                    if (!ruleList.isEmpty()) {
                        processRuleSet();
                        ruleList = new ArrayList<>();
                    }
                    setLeftIdAndPushToStack(rule);
                    break;
                }
                case TWO, FIVE, SIX: {
                    rule.setLeftId(++id);
                    rule.setRightId(++id);
                    break;
                }
                case THREE: {
                    popAndSetRightId(ONE.getValue());
                    setLeftIdAndPushToStack(rule);
                    break;
                }
                case FOUR: {
                    popAndSetRightId(THREE.getValue());
                    setLeftIdAndPushToStack(rule);
                    break;
                }
                case SEVEN: {
                    popAndSetRightId(THREE.getValue());
                    rule.setLeftId(++id);
                    rule.setRightId(++id);
                    break;
                }
                default:
            }
        });
    }

    private void popAndSetRightId(String ruleNo) {
        Rule peeked = ruleStack.peek();
        if (peeked != null && !Objects.equals(peeked.getRuleNo(), ruleNo)) {
            Rule pop = ruleStack.pop();
            pop.setRightId(++id);
        }
    }

    private void setLeftIdAndPushToStack(Rule rule) {
        rule.setLeftId(++id);
        ruleStack.push(rule);
    }

    private void processRuleSet() {
        while (!ruleStack.isEmpty()) {
            ruleStack.pop().setRightId(++id);
        }
        DbUtil.startTransaction();
        ruleList.forEach(ruleSetRepository::insertRule);
        DbUtil.commitTransaction();
    }
}
