package io.reactivestax.domainmodel.repository;

import io.reactivestax.domainmodel.repository.entity.Rule;
import io.reactivestax.domainmodel.util.DbUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RuleSetRepository {
    private static RuleSetRepository instance;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private RuleSetRepository() {
    }

    public static synchronized RuleSetRepository getInstance() {
        if (instance == null) {
            instance = new RuleSetRepository();
        }

        return instance;
    }

    public void insertRule(List<io.reactivestax.domainmodel.model.Rule> ruleList) {
        List<io.reactivestax.domainmodel.model.Rule> ruleListCp = ruleList.stream().toList();
        executorService.submit(() -> {
            DbUtil dbUtil = DbUtil.getInstance();
            dbUtil.startTransaction();
            ruleListCp.forEach(rule -> {
                Rule ruleEntity = getRuleSetEntity(rule);
                dbUtil.getConnection().persist(ruleEntity);
            });
            dbUtil.commitTransaction();
        });
    }

    public List<io.reactivestax.domainmodel.model.Rule> readRules() {
        Session session = DbUtil.getInstance().getConnection();
        List<Rule> ruleEntityList = session.createQuery("from Rule", Rule.class).getResultList();
        return ruleEntityList.stream().map(this::getRuleSetModel).toList();
    }

    private Rule getRuleSetEntity(io.reactivestax.domainmodel.model.Rule rule) {
        return Rule.builder()
                .ruleSetId(rule.getRuleSetId())
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
