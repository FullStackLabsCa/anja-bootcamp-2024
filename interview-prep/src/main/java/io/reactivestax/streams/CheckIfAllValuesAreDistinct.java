package io.reactivestax.streams;

import java.util.List;

public class CheckIfAllValuesAreDistinct {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 1, 2, 6);
        List<Integer> list1 = List.of(1, 2, 3, 4, 5, 6);

        System.out.println(list.stream().distinct().toList().size() == list.size());
        System.out.println(list1.stream().distinct().toList().size() == list1.size());
    }
}
