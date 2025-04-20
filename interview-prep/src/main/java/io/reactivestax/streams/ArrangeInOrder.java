package io.reactivestax.streams;

import java.util.Comparator;
import java.util.List;

public class ArrangeInOrder {

    public static void main(String[] args) {
        List<Integer> list = List.of(23, 43, 532, 1, 2, 63, 632, 123);

        System.out.println(list.stream().sorted().toList());

        System.out.println(list.stream().sorted(Comparator.reverseOrder()).toList());
    }
}
