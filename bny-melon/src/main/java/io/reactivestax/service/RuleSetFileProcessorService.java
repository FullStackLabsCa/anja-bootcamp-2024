package io.reactivestax.service;

import io.reactivestax.model.RuleSet;
import io.reactivestax.repository.RuleSetRepository;
import io.reactivestax.util.DbUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class RuleSetFileProcessorService implements RuleSetFileProcessor {
    private long id = 0;
    private final Deque<RuleSet> ruleSetStack = new ArrayDeque<>();
    private List<RuleSet> ruleSetList = new ArrayList<>();
    private final RuleSetRepository ruleSetRepository = new RuleSetRepository();
    private String root = "";
    private final Logger logger = Logger.getLogger(RuleSetFileProcessorService.class.getName());

    @Override
    public void readAndProcessRuleSetFile() {
        try (Stream<String> stream = Files.lines(Path.of("src/main/resources/rulesetfiles/bony_smb_ruleset_d100914" +
                ".data"))) {
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
        switch (ruleSet.getRuleNo()) {
            case "01": {
                if (!ruleSetList.isEmpty()) {
                    processRuleSet();
                    ruleSetList = new ArrayList<>();
                }
                setLeftIdAndPushToStack(ruleSet);
                break;
            }
            case "02", "05", "06": {
                ruleSet.setLeftId(++id);
                ruleSet.setRightId(++id);
                break;
            }
            case "03": {
                popAndSetRightId("01");
                setLeftIdAndPushToStack(ruleSet);
                break;
            }
            case "04": {
                popAndSetRightId("03");
                setLeftIdAndPushToStack(ruleSet);
                break;
            }
            case "07": {
                popAndSetRightId("03");
                ruleSet.setLeftId(++id);
                ruleSet.setRightId(++id);
                break;
            }
            default:
        }
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
