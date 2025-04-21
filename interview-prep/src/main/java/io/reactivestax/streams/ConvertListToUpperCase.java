package io.reactivestax.streams;

import java.util.List;

public class ConvertListToUpperCase {
    public static void main(String[] args) {
        List<String> stringList = List.of("12hkj35", "ihk345", "123", "456");

        System.out.println(stringList.stream().map(String::toUpperCase).toList());
    }
}
