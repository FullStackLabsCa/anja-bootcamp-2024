package io.reactivestax.streams;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RemoveDuplicates {

    public static void main(String[] args) {

        Set<String> set = new LinkedHashSet<>();

        String str = "my name is anant jain and my brother's name is siddharth jain";

        System.out.println(Arrays.stream(str.split(" ")).distinct().collect(Collectors.joining(" ")));
    }
}
