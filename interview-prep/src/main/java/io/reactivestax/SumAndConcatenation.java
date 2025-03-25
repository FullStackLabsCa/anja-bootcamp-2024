package io.reactivestax;

import java.util.List;

public class SumAndConcatenation {

    public static void main(String[] args) {

//        8. Reduce - Sum and Concatenation
//        Problem:
//        Given a list of integers, use the reduce() function to:
//
//        Find the sum of all numbers
//
//        Concatenate all numbers into a single string

        List<Integer> list = List.of(1, 2, 3, 6);

        Integer i = list.stream().reduce(Integer::sum).orElseThrow();

        System.out.println(i);

        System.out.println(list.stream().map(Object::toString).reduce((num, str) -> num + str).orElseThrow());

    }
}
