package io.reactivestax.parser.repository;

import io.reactivestax.parser.repository.entity.RuleSet;
import io.reactivestax.parser.util.DbUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.stream.Stream;

public class RuleSetRepository {
    public List<io.reactivestax.parser.model.RuleSet> readRuleSet() {
        Session session = DbUtil.getConnection();
        Stream<RuleSet> fromRuleSet = session.createQuery("from RuleSet", RuleSet.class).getResultStream();
        return fromRuleSet.map(this::getRuleSetModel).toList();
    }

    private io.reactivestax.parser.model.RuleSet getRuleSetModel(RuleSet ruleSet) {
        return io.reactivestax.parser.model.RuleSet.builder()
                .ruleNo(ruleSet.getRuleNo())
                .ruleValue(ruleSet.getRuleValue())
                .build();
    }
}
