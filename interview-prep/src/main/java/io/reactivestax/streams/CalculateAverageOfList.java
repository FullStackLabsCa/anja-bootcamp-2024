package io.reactivestax.streams;

import java.util.List;

public class CalculateAverageOfList {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        System.out.println(list.stream().mapToInt(Integer::intValue).average().getAsDouble());
    }
}
