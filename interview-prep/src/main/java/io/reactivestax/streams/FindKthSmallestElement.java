package io.reactivestax.streams;

import java.util.List;

public class FindKthSmallestElement {
    public static void main(String[] args) {
        List<Integer> list = List.of(7, 1, 6, 2, 1, 3, 4, 5);

        System.out.println(list.stream().sorted().toList().get(3-1));
    }
}
