package io.reactivestax.streams;

import java.util.List;

public class SumOfDistinct {
    public static void main(String[] args) {
        List<Integer> list = List.of(23, 43, 532, 1, 2, 63, 632, 123);

        System.out.println(list.stream().distinct().reduce(Integer::sum).get());
    }
}
