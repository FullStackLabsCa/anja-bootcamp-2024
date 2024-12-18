package io.reactivestax.domainmodel.repository;

import io.reactivestax.domainmodel.repository.entity.Rule;
import io.reactivestax.domainmodel.util.DbUtil;
import org.hibernate.Session;

import java.util.List;

public class RuleSetRepository {
    public void insertRule(io.reactivestax.domainmodel.model.Rule rule) {
        Rule ruleEntity = getRuleSetEntity(rule);
        Session session = DbUtil.getConnection();
        session.persist(ruleEntity);
    }

    public List<io.reactivestax.domainmodel.model.Rule> readRules() {
        Session session = DbUtil.getConnection();
        List<Rule> ruleEntityList = session.createQuery("from Rule", Rule.class).getResultList();
        return ruleEntityList.stream().map(this::getRuleSetModel).toList();
    }

    private Rule getRuleSetEntity(io.reactivestax.domainmodel.model.Rule rule) {
        return Rule.builder()
                .ruleNo(rule.getRuleNo())
                .ruleValue(rule.getRuleValue())
                .leftId(rule.getLeftId())
                .rightId(rule.getRightId())
                .build();
    }

    private io.reactivestax.domainmodel.model.Rule getRuleSetModel(Rule rule) {
        return io.reactivestax.domainmodel.model.Rule.builder()
                .ruleNo(rule.getRuleNo())
                .ruleValue(rule.getRuleValue())
                .build();
    }
}
