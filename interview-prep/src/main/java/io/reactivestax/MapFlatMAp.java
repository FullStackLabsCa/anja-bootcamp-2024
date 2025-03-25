package io.reactivestax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapFlatMAp {

    public static void main(String[] args) {
//        7. Map and FlatMap
//        Problem:
//        Given a list of List<String> (nested lists), flatten it into a single list of strings using Java 8 Streams.
        List<List<String>> list = new ArrayList<>(List.of(List.of("afaf", "fafaf"), List.of("afa", "faf"),
                List.of("dfs", "fadf")));

        List<String> list1 = list.stream().flatMap(Collection::stream).toList();

        System.out.println(list1);
    }
}
