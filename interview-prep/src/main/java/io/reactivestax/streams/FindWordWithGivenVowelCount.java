package io.reactivestax.streams;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FindWordWithGivenVowelCount {

    public static void main(String[] args) {

        List<Character> characters = List.of('a', 'e', 'i', 'o', 'u');

        int vowelCount = 2;

        String str = "my name is anant jain and my brother's name is siddharth jain";

//        System.out.println(Arrays.stream(str.split(" "))
//                .distinct()
//                .filter(st -> {
//                    int length = st.chars()
//                            .filter(ch -> characters.contains((char) ch))
//                            .toArray().length;
//
//                    return length == vowelCount;
//                })
//                .toList());

        System.out.println(Arrays.stream(str.split(" "))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(s -> s.chars().filter(ch -> characters.contains((char) ch)).toArray().length))));
    }

}
