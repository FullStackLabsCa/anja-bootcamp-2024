package io.reactivestax;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class CountCharacters {
    public static void main(String[] args) {
        String str = "The the quick brown fox jumped over the lazy dog. It it didn't see that that the dog was actually awake. The fox fox looked back back, surprised surprised by the sudden movement. It was was a strange strange morning in the the forest.";

        countCharsJava8(str);

        countChars(str);
    }

    private static Map<Character, Integer> countCharsJava8(String str) {
        Map<Character, Integer> collect = str.toLowerCase().chars()
                .filter(Character::isLetterOrDigit)
                .mapToObj(ch -> (char) ch)
                .collect(Collectors.toMap(ch -> ch, ch -> 1, Integer::sum));

        Character key = collect.entrySet().stream().min(Comparator.comparingInt(Map.Entry::getValue)).orElseThrow().getKey();
        System.out.println(key);
        return collect;
    }

    private static Map<Character, Integer> countChars(String str) {
        String st = str.toLowerCase().replaceAll(" ", "");
        char[] charArray = st.toCharArray();
        return null;
    }
}
