package io.reactivestax.problems;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FirstNonRepeatingCharacter {
    public static void main(String[] args) {
        String str = "Anant Jain";

        Map.Entry<Character, Integer> characterIntegerEntry = str.toLowerCase().chars().mapToObj(ch -> (char) ch)
                .collect(Collectors.toMap(Function.identity(), v -> 1, Integer::sum, LinkedHashMap::new))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 1)
                .findFirst()
                .orElseGet(null);
        System.out.println(characterIntegerEntry.getKey());
    }
}
