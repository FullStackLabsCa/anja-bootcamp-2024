package io.reactivestax;

import java.util.Optional;

public class OptionalString {
    public static void main(String[] args) {
        System.out.println(getOptionalString(Optional.of("anant jain")));
        System.out.println(getOptionalString(Optional.empty()));
    }

    public static String getOptionalString(Optional<String> str) {
        return str.orElseGet(() -> "DEFAULT").toUpperCase();
    }
}
