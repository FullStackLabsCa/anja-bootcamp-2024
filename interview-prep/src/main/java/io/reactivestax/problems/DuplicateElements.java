package io.reactivestax.problems;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicateElements {
    public static void main(String[] args) {
//        Find Duplicate Elements in a List
//        Given a list of integers, find all the duplicate elements.

        List<Integer> list = List.of(1, 2, 3, 4, 1, 3, 2, 5, 3, 5, 6, 7, 8);
        Set<Integer> set = new HashSet<>();

        List<Integer> list1 = list.stream().filter(num -> !set.add(num)).toList();

        System.out.println(list1);
    }
}
