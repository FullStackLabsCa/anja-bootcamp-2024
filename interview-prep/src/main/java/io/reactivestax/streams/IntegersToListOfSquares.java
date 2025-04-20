package io.reactivestax.streams;

import java.util.List;

public class IntegersToListOfSquares {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7);

        System.out.println(list.stream().map(num -> Math.round(Math.pow(num, 2))).toList());
    }
}
