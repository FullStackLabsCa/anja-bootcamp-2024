package io.reactivestax.repository;

import io.reactivestax.repository.entity.RuleSet;
import io.reactivestax.util.DbUtil;
import org.hibernate.Session;

public class RuleSetRepository {
    public void insertRuleSet(io.reactivestax.model.RuleSet ruleSet) {
        RuleSet ruleSetEntity = getRuleSetEntity(ruleSet);
        Session session = DbUtil.getConnection();
        session.persist(ruleSetEntity);
    }

    private RuleSet getRuleSetEntity(io.reactivestax.model.RuleSet ruleSet) {
        return RuleSet.builder()
                .ruleNo(ruleSet.getRuleNo())
                .ruleValue(ruleSet.getRuleValue())
                .leftId(ruleSet.getLeftId())
                .rightId(ruleSet.getRightId())
                .root(ruleSet.getRoot())
                .build();
    }
}
