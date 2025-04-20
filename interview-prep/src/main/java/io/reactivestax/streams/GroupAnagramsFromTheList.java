package io.reactivestax.streams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GroupAnagramsFromTheList {
    public static void main(String[] args) {
        List<String> stringList = List.of("show", "whos", "hows", "dance", "edanc", "my");

        System.out.println(stringList.stream().collect(Collectors.groupingBy(str -> Arrays.stream(str.split("")).sorted().collect(Collectors.joining("")),
                Collectors.toList())));
    }
}
