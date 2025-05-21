package io.reactivestax.streams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListToMap {
    public static void main(String[] args) {
        List<String> list = List.of("apple", "apricot", "banana");


        Map<String, String> collect = list.stream().collect(Collectors.toMap(str -> String.valueOf(str.charAt(0)), str -> str, (key1, key2)-> key2));

        System.out.println(collect);

    }
}
