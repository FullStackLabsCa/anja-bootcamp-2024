package io.reactivestax.streams;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SortMapByValueDesc {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();

        map.put("anb", 112);
        map.put("ab", 121);
        map.put("nb", 132);
        map.put("b", 123);

        LinkedHashMap<String, Integer> collect = map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (key1, key2) -> key1, LinkedHashMap::new));

        System.out.println(collect);
    }
}
