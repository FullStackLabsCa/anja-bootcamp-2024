package io.reactivestax.streams;

import java.util.List;

public class PrintListContainingOnlyDigits {
    public static void main(String[] args) {
        List<String> stringList = List.of("12hkj35", "ihk345");

        System.out.println(stringList.stream().map(str -> str.replaceAll("\\D+", "")).toList());
    }
}
