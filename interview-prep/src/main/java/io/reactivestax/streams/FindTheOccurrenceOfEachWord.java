package io.reactivestax.streams;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FindTheOccurrenceOfEachWord {

    public static void main(String[] args) {
        String str = "my name is anant jain and my brother's name is siddharth jain";

        Map<String, Long> collect = Arrays.stream(str.split(" ")).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        System.out.println(collect);
    }

}
