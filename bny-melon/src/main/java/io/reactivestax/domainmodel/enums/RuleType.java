package io.reactivestax.domainmodel.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
public enum RuleType {
    ONE("01"), TWO("02"), THREE("03"), FOUR("04"), FIVE("05"), SIX("06"), SEVEN("07");

    private final String value;

    RuleType(String value) {
        this.value = value;
    }

    public static Optional<RuleType> getEnumValue(String ruleNo) {
        return Arrays.stream(RuleType.values()).filter(rule -> Objects.equals(rule.getValue(), ruleNo)).findFirst();
    }
}
