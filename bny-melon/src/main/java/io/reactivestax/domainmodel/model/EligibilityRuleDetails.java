package io.reactivestax.domainmodel.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Getter
public class EligibilityRuleDetails extends Rule {
    private final List<SetValues> setValuesList = new ArrayList<>();
    private final List<ConcentrationLimit> concentrationLimitList = new ArrayList<>();

    public void addSetValues(SetValues setValues) {
        setValuesList.add(setValues);
    }

    public void addConcentrationLimit(ConcentrationLimit concentrationLimit) {
        concentrationLimitList.add(concentrationLimit);
    }
}
