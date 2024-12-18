package io.reactivestax.withoutdomainmodel.parser.repository;

import io.reactivestax.withoutdomainmodel.parser.repository.entity.RuleSet;
import io.reactivestax.withoutdomainmodel.parser.util.DbUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.stream.Stream;

public class RuleSetRepository {
    public List<io.reactivestax.withoutdomainmodel.parser.model.RuleSet> readRuleSet() {
        Session session = DbUtil.getConnection();
        Stream<RuleSet> fromRuleSet = session.createQuery("from RuleSet", RuleSet.class).getResultStream();
        return fromRuleSet.map(this::getRuleSetModel).toList();
    }

    private io.reactivestax.withoutdomainmodel.parser.model.RuleSet getRuleSetModel(RuleSet ruleSet) {
        return io.reactivestax.withoutdomainmodel.parser.model.RuleSet.builder()
                .ruleNo(ruleSet.getRuleNo())
                .ruleValue(ruleSet.getRuleValue())
                .build();
    }
}
