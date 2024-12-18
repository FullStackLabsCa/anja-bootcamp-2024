package io.reactivestax.withoutdomainmodel.persister.repository;

import io.reactivestax.withoutdomainmodel.persister.repository.entity.RuleSet;
import io.reactivestax.withoutdomainmodel.persister.util.DbUtil;
import org.hibernate.Session;

public class RuleSetRepository {
    public void insertRuleSet(io.reactivestax.withoutdomainmodel.persister.model.RuleSet ruleSet) {
        RuleSet ruleSetEntity = getRuleSetEntity(ruleSet);
        Session session = DbUtil.getConnection();
        session.persist(ruleSetEntity);
    }

    private RuleSet getRuleSetEntity(io.reactivestax.withoutdomainmodel.persister.model.RuleSet ruleSet) {
        return RuleSet.builder()
                .ruleNo(ruleSet.getRuleNo())
                .ruleValue(ruleSet.getRuleValue())
                .leftId(ruleSet.getLeftId())
                .rightId(ruleSet.getRightId())
                .root(ruleSet.getRoot())
                .build();
    }
}
