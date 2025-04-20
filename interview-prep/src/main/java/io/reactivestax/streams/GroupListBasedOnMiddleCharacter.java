package io.reactivestax.streams;

import java.util.List;
import java.util.stream.Collectors;

public class GroupListBasedOnMiddleCharacter {
    public static void main(String[] args) {
        List<String> list = List.of("anant", "wew", "new", "nan", "asdfd", "han");

        System.out.println(list.stream().collect(Collectors.groupingBy(str -> str.charAt(str.length() / 2), Collectors.toList())));
    }
}
