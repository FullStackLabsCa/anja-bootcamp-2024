package io.reactivestax.streams;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListOfStringToMapWithLengthAsValue {
    public static void main(String[] args) {
        List<String> name = List.of("name", "anant", "ankit", "hagu");

        LinkedHashMap<String, Integer> collect = name.stream().collect(Collectors.toMap(Function.identity(), str -> str.length(), (key1,
                                                                                                                                   key2) -> key1, LinkedHashMap::new));
        System.out.println(collect);
    }
}
