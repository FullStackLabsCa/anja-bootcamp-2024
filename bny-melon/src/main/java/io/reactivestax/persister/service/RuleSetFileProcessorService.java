package io.reactivestax.persister.service;

import io.reactivestax.persister.enums.RuleNo;
import io.reactivestax.persister.model.RuleSet;
import io.reactivestax.persister.repository.RuleSetRepository;
import io.reactivestax.persister.util.DbUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static io.reactivestax.persister.enums.RuleNo.ONE;
import static io.reactivestax.persister.enums.RuleNo.THREE;

public class RuleSetFileProcessorService implements RuleSetFileProcessor {
    private long id = 0;
    private final Deque<RuleSet> ruleSetStack = new ArrayDeque<>();
    private List<RuleSet> ruleSetList = new ArrayList<>();
    private final RuleSetRepository ruleSetRepository = new RuleSetRepository();
    private String root = "";
    private final Logger logger = Logger.getLogger(RuleSetFileProcessorService.class.getName());

    @Override
    public void readAndProcessRuleSetFile() {
        try (Stream<String> stream = Files.lines(Path.of("src/main/resources/rulesetfiles/bony_smb_ruleset_d100914.data"))) {
            stream.forEach(rule -> {
                String ruleNo = rule.substring(0, 2);
                String ruleValue = rule.substring(2);
                if (ruleNo.equals("01")) {
                    root = ruleValue;
                }
                RuleSet ruleSet = RuleSet.builder()
                        .ruleNo(ruleNo)
                        .ruleValue(ruleValue)
                        .root(root)
                        .build();
                prepareRuleSetList(ruleSet);
                ruleSetList.add(ruleSet);
            });
            processRuleSet();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    private void prepareRuleSetList(RuleSet ruleSet) {
        RuleNo.getEnumValue(ruleSet.getRuleNo()).ifPresent(ruleNo -> {
            switch (ruleNo) {
                case ONE: {
                    if (!ruleSetList.isEmpty()) {
                        processRuleSet();
                        ruleSetList = new ArrayList<>();
                    }
                    setLeftIdAndPushToStack(ruleSet);
                    break;
                }
                case TWO, FIVE, SIX: {
                    ruleSet.setLeftId(++id);
                    ruleSet.setRightId(++id);
                    break;
                }
                case THREE: {
                    popAndSetRightId(ONE.getValue());
                    setLeftIdAndPushToStack(ruleSet);
                    break;
                }
                case FOUR: {
                    popAndSetRightId(THREE.getValue());
                    setLeftIdAndPushToStack(ruleSet);
                    break;
                }
                case SEVEN: {
                    popAndSetRightId(THREE.getValue());
                    ruleSet.setLeftId(++id);
                    ruleSet.setRightId(++id);
                    break;
                }
                default:
            }
        });
    }

    private void popAndSetRightId(String ruleNo) {
        RuleSet peeked = ruleSetStack.peek();
        if (peeked != null && !Objects.equals(peeked.getRuleNo(), ruleNo)) {
            RuleSet pop = ruleSetStack.pop();
            pop.setRightId(++id);
        }
    }

    private void setLeftIdAndPushToStack(RuleSet ruleSet) {
        ruleSet.setLeftId(++id);
        ruleSetStack.push(ruleSet);
    }

    private void processRuleSet() {
        while (!ruleSetStack.isEmpty()) {
            ruleSetStack.pop().setRightId(++id);
        }
        DbUtil.startTransaction();
        ruleSetList.forEach(ruleSetRepository::insertRuleSet);
        DbUtil.commitTransaction();
    }
}
