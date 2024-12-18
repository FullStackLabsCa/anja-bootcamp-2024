package io.reactivestax.withoutdomainmodel.persister.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
public enum RuleNo {
    ONE("01"), TWO("02"), THREE("03"), FOUR("04"), FIVE("05"), SIX("06"), SEVEN("07");

    private final String value;

    RuleNo(String value) {
        this.value = value;
    }

    public static Optional<RuleNo> getEnumValue(String ruleNo) {
        return Arrays.stream(RuleNo.values()).filter(rule -> Objects.equals(rule.getValue(), ruleNo)).findFirst();
    }
}
