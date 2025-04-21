package io.reactivestax.streams;

import java.util.List;
import java.util.stream.Stream;

public class Intersection {

    public static void main(String[] args) {
        List<Integer> list = List.of(2, 3, 5, 6, 7, 8);
        List<Integer> list2 = List.of(1, 3, 4, 5, 6, 7);

        System.out.println(Stream.concat(list.stream(), list2.stream()).filter(num -> list.contains(num) && list2.contains(num)).distinct().toList());
    }
}
