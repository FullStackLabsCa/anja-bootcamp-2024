package io.reactivestax.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoveAllZeroesToBeginning {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 30, 0, 2, 0, 1, 4, 0);

        Map<Boolean, List<Integer>> collect = list.stream().collect(Collectors.groupingBy(num -> num == 0, Collectors.toList()));

        List<Integer> li = new ArrayList<>();

        li.addAll(collect.get(true));
        li.addAll(collect.get(false));

        System.out.println(li);
    }
}
