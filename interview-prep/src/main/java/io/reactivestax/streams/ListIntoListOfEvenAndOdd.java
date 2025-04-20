package io.reactivestax.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListIntoListOfEvenAndOdd {

    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Map<Boolean, List<Integer>> map = new HashMap<>();

        map.put(true, new ArrayList<>());
        map.put(false, new ArrayList<>());

        list.forEach(num -> map.get(num % 2 == 0).add(num));

        Map<Boolean, List<Integer>> collect = list.stream().collect(Collectors.groupingBy(n -> n % 2 == 0, Collectors.toList()));

        System.out.println(map);
        System.out.println(collect);
    }
}
