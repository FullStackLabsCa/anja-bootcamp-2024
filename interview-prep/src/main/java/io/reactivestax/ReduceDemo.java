package io.reactivestax;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ReduceDemo {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4);

        Optional<Integer> reduced = list.stream().reduce(Integer::sum);

        System.out.println(reduced.get());
    }
}
