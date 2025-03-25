package io.reactivestax;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Duplicates {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 1, 3, 5, 2, 3);
        Set<Integer> set = new HashSet<>();

        List<Integer> list1 = list.stream().filter(num -> !set.add(num)).toList();

        System.out.println(list1);
    }
}
