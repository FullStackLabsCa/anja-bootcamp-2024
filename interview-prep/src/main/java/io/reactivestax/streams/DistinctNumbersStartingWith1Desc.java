package io.reactivestax.streams;

import java.util.Comparator;
import java.util.List;

public class DistinctNumbersStartingWith1Desc {
    public static void main(String[] args) {
        List<Integer> list = List.of(12, 34, 11, 34, 67, 121, 121, 52, 78, 114, 565, 1643, 11);

        System.out.println(list.stream().distinct().filter(num -> num.toString().startsWith("1")).sorted(Comparator.reverseOrder()).toList());
    }
}
