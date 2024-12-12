package io.reactivestax.repository;

import io.reactivestax.entity.RuleSet;
import io.reactivestax.util.DbUtil;
import org.hibernate.Session;

public class RuleSetRepository {
    public void insertRuleSet(RuleSet ruleSet) {
        Session session = DbUtil.getConnection();
        session.persist(ruleSet);
    }
}
