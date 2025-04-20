package io.reactivestax.streams;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GroupNumbersByTheRange {

    public static void main(String[] args) {
        Integer[] arr = {2, 3, 10,11, 14, 20, 24, 30, 34, 40, 44, 50, 54};


        System.out.println(Arrays.stream(arr).collect(Collectors.groupingBy((Integer num) -> (num / 10) * 10, Collectors.toList())));
    }
}
